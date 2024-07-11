#pragma once

#include <jni.h>
#include <string>
#include <android/log.h>
#include <libx264/x264.h>
#include "LibRtmp.cxx"

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_librtmp",FORMAT,##__VA_ARGS__)
#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1x264encoder_JniImp_##name
extern "C" {
#include <libavutil/time.h>
}
class X264Encode {
private:
    x264_param_t param;
    x264_picture_t *pic_i420;
    x264_t *videoCodec;
    int width;
    int height;
    int fps;
    int bitrate;


    void initX264() {
        //setting x264 params
        x264_param_default_preset(&param, "ultrafast", "zerolatency");
        param.i_level_idc = 32;
        //input format
        param.i_csp = X264_CSP_I420;
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
        x264_picture_alloc(pic_i420, X264_CSP_I420, width, height);
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
        packet->m_nTimeStamp = tms;
        packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
        packet->m_nChannel = 0x10;
        packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
        return packet;
    }


public:

    X264Encode(int width, int height, int fps, int bitrate) {
        this->pic_i420 = new x264_picture_t();
        this->width = width;
        this->height = height;
        this->fps = fps;
        this->bitrate = bitrate;
        initX264();

    }

    ~X264Encode() {
        x264_encoder_close(videoCodec);
        delete pic_i420;
    }

    void encodeI420(int8_t *y, int8_t *u, int8_t *v) {
        memcpy(pic_i420->img.plane[0], y, width * height); // Y分量
        memcpy(pic_i420->img.plane[1], u, width * height / 4); // U分量
        memcpy(pic_i420->img.plane[2], v, width * height / 4); // V分量
        x264_nal_t *pp_nal;//NAL
        int pi_nal;//amount of NAL
        x264_picture_t pic_out;
        x264_encoder_encode(videoCodec, &pp_nal, &pi_nal, pic_i420, &pic_out);
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
                queue->pushPacket(spsPkt);
            } else {
                RTMPPacket* framePkt = createFrame(nal.i_type, nal.p_payload, nal.i_payload, av_gettime()/1000);
                queue->pushPacket(framePkt);
            }
        }
    }

    void encodeNV12(int8_t *y, int8_t *uv) {
        memcpy(pic_i420->img.plane[0], y, width * height); // Y分量
        //nv12 -> i420
        for (int i = 0; i < width * height / 4; ++i) {
            pic_i420->img.plane[1][i] = uv[i * 2];
            pic_i420->img.plane[2][i] = uv[i * 2 + 1];
        }
        x264_nal_t *pp_nal;//NAL
        int pi_nal;//amount of NAL
        x264_picture_t pic_out;
        x264_encoder_encode(videoCodec, &pp_nal, &pi_nal, pic_i420, &pic_out);
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
                queue->pushPacket(spsPkt);
            } else {
                RTMPPacket* framePkt = createFrame(nal.i_type, nal.p_payload, nal.i_payload, av_gettime()/1000);
                queue->pushPacket(framePkt);
            }
        }

    }


};
