#include <jni.h>
#include <string>

extern "C" {
#include "librtmp/rtmp.h"
}

#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_hardrtmp",FORMAT,##__VA_ARGS__)
RTMP *rtmp = nullptr;

struct SpsPps {
    int sps_len;
    int pps_len;
    int8_t *sps;
    int8_t *pps;
};

SpsPps *live = new SpsPps();

void saveSpsPps(int8_t *data, int len, SpsPps *live) {
    for (int i = 0; i < len; i++) {
        if (i + 4 < len) {
            if (data[i] == 0x00 && data[i + 1] == 0x00
                && data[i + 2] == 0x00
                && data[i + 3] == 0x01) {
                if (data[i + 4] == 0x68) {
                    live->sps_len = i - 4;
                    live->sps = static_cast<int8_t *>(malloc(live->sps_len));
                    memcpy(live->sps, data + 4, live->sps_len);
                    live->pps_len = len - (4 + live->sps_len) - 4;
                    live->pps = static_cast<int8_t *>(malloc(live->pps_len));
                    memcpy(live->pps, data + 4 + live->sps_len + 4, live->pps_len);
                    break;
                }
            }
        }
    }
}


RTMPPacket *createSpsPackage(uint8_t *sps, uint8_t *pps, int sps_len, int pps_len) {
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

RTMPPacket *createVideoPackage(int8_t *buf, int len, const long tms) {
    buf += 4;
    len -= 4;
    int body_size = len + 9;
    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
    RTMPPacket_Alloc(packet, len + 9);
    packet->m_body[0] = 0x27;//P Frame flag
    if (buf[0] == 0x65) {//whether I Frame
        packet->m_body[0] = 0x17;//I Frame flag
    }
    packet->m_body[1] = 0x01;
    packet->m_body[2] = 0x00;
    packet->m_body[3] = 0x00;
    packet->m_body[4] = 0x00;
    packet->m_body[5] = (len >> 24) & 0xff;
    packet->m_body[6] = (len >> 16) & 0xff;
    packet->m_body[7] = (len >> 8) & 0xff;
    packet->m_body[8] = (len) & 0xff;
    memcpy(&packet->m_body[9], buf, len);
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = body_size;
    packet->m_nChannel = 0x04;
    packet->m_nTimeStamp = tms;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = rtmp->m_stream_id;
    return packet;

}

void connectRTMP(const char *url) {
    rtmp = RTMP_Alloc();
    RTMP_Init(rtmp);
    rtmp->Link.timeout = 10;
    RTMP_SetupURL(rtmp, (char *) url);
    RTMP_EnableWrite(rtmp);
    RTMP_Connect(rtmp, 0);
    int re = RTMP_ConnectStream(rtmp, 0);
    if (!re) {
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        LOGE("connect failed");
    }
    LOGE("connect success");
}

RTMPPacket *createAudioPacket(int8_t *data, int len, int64_t tms) {
    int bodySize = 2 + len;
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, bodySize);
    packet->m_body[0] = 0xAF;
    packet->m_body[1] = 0x01;
    memcpy(&packet->m_body[2], data, len);
    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = bodySize;
    packet->m_nTimeStamp = tms;
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x11;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return packet;
}

void sendPacket(RTMPPacket *packet) {
    int re = RTMP_SendPacket(rtmp, packet, 1);
    RTMPPacket_Free(packet);
    free(packet);
    if (!re) {
        RTMP_Close(rtmp);
        RTMP_Free(rtmp);
        LOGE("send packet failed");
        connectRTMP("rtmp://139.224.68.119:1935/rtmplive_demo/hls");//reconnect
    }
}

void sendVideo(int8_t *buf, int len, long tms) {
    if (buf[4] == 0x67) {//sps pps
        saveSpsPps(buf, len, live);
    } else {//I and P frames
        if (buf[4] == 0x65) { //if it is I frame then send sps pps
            RTMPPacket *packet = createSpsPackage((uint8_t *) live->sps, (uint8_t *) live->pps,
                                                  live->sps_len, live->pps_len);
            sendPacket(packet);
        }
        RTMPPacket *packet = createVideoPackage(buf, len, tms);
        sendPacket(packet);
    }
}

void sendAudio(int8_t *buf, int len, int tms) {
    RTMPPacket *packet = createAudioPacket(buf, len, tms);
    sendPacket(packet);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1hardrtmp_JniImp_init(JNIEnv *env, jclass thiz, jstring url) {
    const char *rtmpUrl = env->GetStringUTFChars(url, nullptr);
    connectRTMP(rtmpUrl);
    env->ReleaseStringUTFChars(url, rtmpUrl);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1hardrtmp_JniImp_pushH264(JNIEnv *env, jclass clazz, jbyteArray data,
                                                 jint len, jlong tms) {
    jbyte *buf = env->GetByteArrayElements(data, nullptr);
    sendVideo((int8_t *) buf, len, tms);
    env->ReleaseByteArrayElements(data, buf, 0);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1hardrtmp_JniImp_pushAAC(JNIEnv *env, jclass clazz, jbyteArray data,
                                                jint len, jlong tms) {
    // TODO: implement pushAAC()
    jbyte *buf = env->GetByteArrayElements(data, nullptr);
    sendAudio((int8_t *) buf, len, tms);
    env->ReleaseByteArrayElements(data, buf, 0);
}