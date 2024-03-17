#include <jni.h>
#include <string>
#include <android/log.h>
#include <queue>
#include <mutex>
#include <unistd.h>
#include <thread>
#include <libfaac/faac.h>
#include <libx264/x264.h>


using namespace std;
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "OrgRTMP", __VA_ARGS__)
extern "C" {
#include "librtmp/rtmp.h"
#include "libavutil/time.h"
}

struct Live {
    RTMP *rtmp;
    string url;
    void release() {
        if (rtmp) {
            RTMP_Close(rtmp);
            RTMP_Free(rtmp);
        }
    }
};

Live *live = 0;
void connectRTMP(const char *url) {
    if(live == nullptr)
        live = new Live();
    live->url = string(url);
    live->rtmp = RTMP_Alloc();
    RTMP_Init(live->rtmp);
    live->rtmp->Link.timeout = 10;
    RTMP_SetupURL(live->rtmp, (char *) url);
    RTMP_EnableWrite(live->rtmp);
    RTMP_Connect(live->rtmp, 0);
    int re = RTMP_ConnectStream(live->rtmp, 0);
    if (!re) {
        live->release();
        LOGE("connect failed");
    }
    LOGE("connect success");
}

struct AudioData {
    int8_t *data;
    int len;
    int64_t tms;

    void release() {
        if (data) {
            free(data);
            data = nullptr;
        }
    }
};

struct XNV12Data {
    int8_t *ydata;
    int8_t *uvdata;
    int width;
    int height;
    int len;
    int64_t tms;
    void release() {
        if (ydata) {
            free(ydata);
            ydata = nullptr;
        }
        if (uvdata) {
            free(uvdata);
            uvdata = nullptr;
        }
    }
};

struct FrameData {
    AudioData audioData;
    XNV12Data xnv12Data;
    int type;
};


queue<FrameData> datas;
mutex datasMutex;
bool isRunning = true;
bool isExit[2] = {0};


u_long inputSamples;
faacEncHandle audioCodec = 0;
u_long maxOutputBytes;

x264_t *videoCodec = 0;
x264_picture_t *pic_in = 0;

RTMPPacket *createAudioPacket(AudioData data) {
    int bodySize = 2 + data.len;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    packet->m_body[0] = 0xAF;
    packet->m_body[1] = 0x01;
    memcpy(&packet->m_body[2], data.data, data.len);
    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = bodySize;
    packet->m_nTimeStamp = data.tms / 1000;
    packet->m_nInfoField2 = live->rtmp->m_stream_id;//whats this? answer: stream id
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x11;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return packet;
}



RTMPPacket *createSps(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len) {
    int bodySize = 13 + sps_len + 3 + pps_len;
    auto *packet = new RTMPPacket();
    RTMPPacket_Alloc(packet, bodySize);
    int i = 0;
    // type
    packet->m_body[i++] = 0x17;
    packet->m_body[i++] = 0x00;
    // timestamp
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    //version
    packet->m_body[i++] = 0x01;
    // profile
    packet->m_body[i++] = sps[1];
    packet->m_body[i++] = sps[2];
    packet->m_body[i++] = sps[3];
    packet->m_body[i++] = 0xFF;
    //sps
    packet->m_body[i++] = 0xE1;
    //sps len
    packet->m_body[i++] = (sps_len >> 8) & 0xFF;
    packet->m_body[i++] = sps_len & 0xFF;
    memcpy(&packet->m_body[i], sps, sps_len);
    i += sps_len;
    //pps
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = (pps_len >> 8) & 0xFF;
    packet->m_body[i++] = (pps_len) & 0xFF;
    memcpy(&packet->m_body[i], pps, pps_len);
    //video
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = bodySize;
    packet->m_nChannel = 0x10;
    //sps and pps no timestamp
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_MEDIUM;
    return packet;
}

RTMPPacket *createFrame(int type, uint8_t *payload, int i_payload, int64_t tms) {
    if (payload[2] == 0x00) {
        i_payload -= 4;
        payload += 4;
    } else {
        i_payload -= 3;
        payload += 3;
    }
    int i = 0;
    int bodySize = 9 + i_payload;
    auto *packet = new RTMPPacket();
    RTMPPacket_Alloc(packet, bodySize);

    if (type == NAL_SLICE_IDR) {
        packet->m_body[i++] = 0x17; // 1:Key frame  7:AVC
    } else {
        packet->m_body[i++] = 0x27; // 2:None key frame 7:AVC
    }
    //AVC NALU
    packet->m_body[i++] = 0x01;
    //timestamp
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    //packet len
    packet->m_body[i++] = (i_payload >> 24) & 0xFF;
    packet->m_body[i++] = (i_payload >> 16) & 0xFF;
    packet->m_body[i++] = (i_payload >> 8) & 0xFF;
    packet->m_body[i++] = (i_payload) & 0xFF;

    memcpy(&packet->m_body[i], payload, static_cast<size_t>(i_payload));

    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = bodySize;
    packet->m_nTimeStamp = tms / 1000;
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nChannel = 0x10;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return packet;
}

void sendPacket(RTMPPacket *packet) {
    int re = RTMP_SendPacket(live->rtmp, packet, 1);
    RTMPPacket_Free(packet);
    free(packet);
    if (!re) {
        live->release();
        connectRTMP(live->url.c_str());//reconnect
    }
}

void encodeVideo(XNV12Data data) {
    memcpy(pic_in->img.plane[0], data.ydata, data.width * data.height); // Y分量
    memcpy(pic_in->img.plane[1], data.uvdata, data.width * data.height / 2); // UV分量
    x264_nal_t *pp_nal;//NAL
    int pi_nal;//NAL数量
    x264_picture_t pic_out;//输出图片
    x264_encoder_encode(videoCodec, &pp_nal, &pi_nal, pic_in, &pic_out);
    int pps_len, sps_len = 0;
    uint8_t sps[100];//sps
    uint8_t pps[100];
    for (int i = 0; i < pi_nal; ++i) {
        x264_nal_t nal = pp_nal[i];
        if (nal.i_type == NAL_SPS) {
            sps_len = nal.i_payload - 4;
            memcpy(sps, nal.p_payload + 4, static_cast<size_t>(sps_len));
        } else if (nal.i_type == NAL_PPS) {
            pps_len = nal.i_payload - 4;
            memcpy(pps, nal.p_payload + 4, static_cast<size_t>(pps_len));
            RTMPPacket* spsPkt = createSps(sps, pps, sps_len, pps_len);
            sendPacket(spsPkt);
        } else {
            RTMPPacket* framePkt = createFrame(nal.i_type, nal.p_payload, nal.i_payload, data.tms);
            sendPacket(framePkt);
        }
    }
}






void startLive(string url) {
    isRunning = true;
    isExit[0] = false;

    connectRTMP(url.data());
    while (isRunning) {
        datasMutex.lock();
        if (datas.empty()) {
            datasMutex.unlock();
            usleep(1);
            continue;
        }
        FrameData data = datas.front();
        datas.pop();
        datasMutex.unlock();
        if (data.type == 1) {
            RTMPPacket *packet = createAudioPacket(data.audioData);
            sendPacket(packet);
            data.audioData.release();
        }
        if (data.type == 2) {
            encodeVideo(data.xnv12Data);
            data.xnv12Data.release();
        }
    }
    isExit[0] = true;

}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1orgrtmp_JniImp_pushAudio(JNIEnv *env, jclass clazz, jbyteArray data,
                                                 jint len) {
    jbyte *inbuf = env->GetByteArrayElements(data, 0);
    int8_t *outbuf = (int8_t *) malloc(maxOutputBytes);
    int outsize = faacEncEncode(audioCodec, (int32_t *) inbuf, inputSamples, (uint8_t *) outbuf,
                                maxOutputBytes);
    FrameData frameData;
    frameData.type = 1;
    frameData.audioData.data = outbuf;
    frameData.audioData.tms = av_gettime();
    frameData.audioData.len = outsize;
    while (datas.size() > 100) {
        usleep(10);
    }
    datasMutex.lock();
    datas.push(frameData);
    datasMutex.unlock();
    env->ReleaseByteArrayElements(data, inbuf, 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1orgrtmp_JniImp_pushVideo(JNIEnv *env, jclass clazz, jbyteArray y,
                                                 jbyteArray uv, jint width,
                                                 jint height) {
    jbyte *y_data = env->GetByteArrayElements(y, 0);
    jbyte *uv_data = env->GetByteArrayElements(uv, 0);

    int8_t *ydata = (int8_t *) malloc(width * height);
    int8_t *uvdata = (int8_t *) malloc(width * height / 2);
    memcpy(ydata, y_data, width * height);
    memcpy(uvdata, uv_data, width * height / 2);
    FrameData frameData;
    frameData.type = 2;
    frameData.xnv12Data.ydata = ydata;
    frameData.xnv12Data.uvdata = uvdata;
    frameData.xnv12Data.tms = av_gettime();
    frameData.xnv12Data.width = width;
    frameData.xnv12Data.height = height;
    while (datas.size() > 100) {
        usleep(10);
    }
    datasMutex.lock();
    datas.push(frameData);
    datasMutex.unlock();
    env->ReleaseByteArrayElements(y, y_data, 0);
    env->ReleaseByteArrayElements(uv, uv_data, 0);
}




extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1orgrtmp_JniImp_initFaac(JNIEnv *env, jclass clazz) {

    audioCodec = faacEncOpen(44100, 2, &inputSamples, &maxOutputBytes);
    faacEncConfigurationPtr config = faacEncGetCurrentConfiguration(audioCodec);
    config->mpegVersion = MPEG4;
    config->aacObjectType = LOW;
    config->inputFormat = FAAC_INPUT_16BIT;
    config->outputFormat = 1;
    faacEncSetConfiguration(audioCodec, config);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1orgrtmp_JniImp_initX264(JNIEnv *env, jclass clazz, jint width,
                                                jint height, jint fps, jint bitrate) {
    //setting x264 params
    x264_param_t param;
    x264_param_default_preset(&param, "ultrafast", "zerolatency");
    param.i_level_idc = 32;
    //input format
    param.i_csp = X264_CSP_NV12;
    param.i_width = width;
    param.i_height = height;
    //no B frame
    param.i_bframe = 0;
    //i_rc_method:bitrate control, CQP(constant quality), CRF(constant bitrate), ABR(average bitrate)
    param.rc.i_rc_method = X264_RC_ABR;
    //bitrate(Kbps)
    param.rc.i_bitrate = bitrate / 1024;
    //max bitrate
    param.rc.i_vbv_max_bitrate = bitrate / 1024 * 1.2;
    //unit:kbps
    param.rc.i_vbv_buffer_size = bitrate / 1024;

    //frame rate
    param.i_fps_num = fps;
    param.i_fps_den = 1;
    param.i_timebase_den = param.i_fps_num;
    param.i_timebase_num = param.i_fps_den;
    //using fps
    param.b_vfr_input = 0;
    //key frame interval(GOP)
    param.i_keyint_max = fps * 2;
    //each key frame attaches sps/pps
    param.b_repeat_headers = 1;
    //thread number
    param.i_threads = 1;
    x264_param_apply_profile(&param, "baseline");
    //open encoder
    videoCodec = x264_encoder_open(&param);
    pic_in = new x264_picture_t();
    x264_picture_alloc(pic_in, X264_CSP_NV12, width, height);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1orgrtmp_JniImp_startLive(JNIEnv *env, jclass clazz, jstring url) {
    const char *rtmpUrl = env->GetStringUTFChars(url, 0);
    thread t1(startLive,string(rtmpUrl));
    t1.detach();
    env->ReleaseStringUTFChars(url, rtmpUrl);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1orgrtmp_JniImp_stopLive(JNIEnv *env, jclass clazz) {
    isRunning = false;
    while (!isExit[0]) {
        usleep(1);
    }
    if (audioCodec) {
        faacEncClose(audioCodec);
        audioCodec = nullptr;
    }
    if (videoCodec) {
        x264_encoder_close(videoCodec);
        videoCodec = nullptr;
    }
    if (pic_in) {
        x264_picture_clean(pic_in);
        delete pic_in;
        pic_in = nullptr;
    }
    if (live) {
        live->release();
        delete live;
        live = nullptr;
    }

    while (datas.size() > 0) {
        FrameData data = datas.front();
        datas.pop();
        if (data.type == 1) {
            data.audioData.release();
        }
        if (data.type == 2) {
            data.xnv12Data.release();
        }
    }
}