#pragma once

#include <jni.h>
#include <string>
#include <android/log.h>
#include <libx264/x264.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_x264encoder",FORMAT,##__VA_ARGS__)
#define JNI_FUNC(name) extern "C" JNIEXPORT void JNICALL Java_com_example_zchan_1x264encoder_JniImp_##name

class X264Encoder {
private:
    x264_param_t param;
    x264_picture_t *pic_i420;
    x264_t *videoCodec;
    int width;
    int height;
    int fps;
    int bitrate;
    FILE *fp_dst;

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

public:

    X264Encoder(int width, int height, int fps, int bitrate, char *dstPath) {
        this->pic_i420 = new x264_picture_t();
        this->width = width;
        this->height = height;
        this->fps = fps;
        this->bitrate = bitrate;
        fp_dst = fopen(dstPath, "wb");
        initX264();

    }

    ~X264Encoder() {
        x264_encoder_close(videoCodec);
        delete pic_i420;
        fclose(fp_dst);
    }

    void encodeI420(int8_t *y, int8_t *u, int8_t *v) {
        memcpy(pic_i420->img.plane[0], y, width * height); // Y分量
        memcpy(pic_i420->img.plane[1], u, width * height / 4); // U分量
        memcpy(pic_i420->img.plane[2], v, width * height / 4); // V分量
        x264_nal_t *pp_nal;//NAL
        int pi_nal;//amount of NAL
        x264_picture_t pic_out;
        x264_encoder_encode(videoCodec, &pp_nal, &pi_nal, pic_i420, &pic_out);
        LOGE("pi_nal:%d", pi_nal);
        for (int i = 0; i < pi_nal; ++i) {
            x264_nal_t nal = pp_nal[i];
            fwrite(nal.p_payload, 1, nal.i_payload, fp_dst);
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
        for (int i = 0; i < pi_nal; ++i) {
            if (pp_nal[i].i_type == NAL_SPS) {
                LOGE("NAL_SPS");
            }

            if (pp_nal[i].i_type == NAL_PPS) {
                LOGE("NAL_PPS");
            }

            //I frame
            if (pp_nal[i].i_type == NAL_SLICE_IDR) {
                LOGE("I frame");
            }

            //P frame
            if (pp_nal[i].i_type == NAL_SLICE) {
                LOGE("P frame");
            }

            //B frame
            if (pp_nal[i].i_type == NAL_SLICE_DPA) {
                LOGE("B frame");
            }

            x264_nal_t nal = pp_nal[i];
            fwrite(nal.p_payload, 1, nal.i_payload, fp_dst);
        }

    }


};
