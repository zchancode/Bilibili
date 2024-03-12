#include <jni.h>
#include <string>
#include <android/log.h>

#define TAG "zchan_play2"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

#include <jni.h>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include <malloc.h>
//c++ string
#include <string>
#include <math.h>
#include <queue>
#include <unistd.h>
#include <mutex>
#include <thread>

extern "C" {
#include <libavutil/time.h>
#include <libavformat/avformat.h>
#include <SLES/OpenSLES.h>
#include <SLES/OpenSLES_Android.h>
#include <libswresample/swresample.h>
#include <libavutil/imgutils.h>
#include <libavutil/opt.h>
#include <libswscale/swscale.h>
#include <libavcodec/jni.h>
}


static const char *vertexShader = "#version 300 es\n"
                                  "in vec4 aPosition;\n"
                                  "in vec2 aTexCoord;\n"
                                  "out vec2 vTexCoord;\n"
                                  "void main() {\n"
                                  "vTexCoord = vec2(aTexCoord.x, 1.0 - aTexCoord.y);\n"
                                  "   gl_Position = aPosition;\n"
                                  "}";

static const char *fragYUV420P = "#version 300 es\n"
                                 "precision mediump float;\n"
                                 "        in vec2 vTexCoord;\n"
                                 "        uniform sampler2D yTexture; //输入的材质（不透明灰度，单像素）\n"
                                 "        uniform sampler2D uTexture;\n"
                                 "        uniform sampler2D vTexture;\n"
                                 "        out vec4 fragColor; //输出像素颜色\n"
                                 "        void main() {\n"
                                 "            vec3 yuv;\n"
                                 "            vec3 rgb;\n"
                                 "            yuv.r = texture(yTexture, vTexCoord).r;\n"
                                 "            yuv.g = texture(uTexture, vTexCoord).r - 0.5;\n"
                                 "            yuv.b = texture(vTexture, vTexCoord).r - 0.5;\n"
                                 "            rgb = mat3(1.0, 1.0, 1.0,\n"
                                 "                       0.0, -0.39465, 2.03211,\n"
                                 "                       1.13983, -0.58060, 0.0) * yuv;\n"
                                 "            fragColor = vec4(rgb, 1.0);\n"
                                 "        }";

EGLSurface surface = EGL_NO_SURFACE;
EGLDisplay display = EGL_NO_DISPLAY;
GLuint textures[3] = {0};
ANativeWindow *win = 0;

void initEGL(ANativeWindow *win) {
    display = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    eglInitialize(display, 0, 0);
    EGLConfig config;
    EGLint numConfigs;
    const EGLint attribs[] = {
            EGL_RED_SIZE, 8,
            EGL_GREEN_SIZE, 8,
            EGL_BLUE_SIZE, 8,
            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
            EGL_NONE
    };
    eglChooseConfig(display, attribs, &config, 1, &numConfigs);
    surface = eglCreateWindowSurface(display, config, win, NULL);
    const EGLint ctx_attribs[] = {
            EGL_CONTEXT_CLIENT_VERSION, 2,
            EGL_NONE
    };
    EGLContext context = eglCreateContext(display, config, EGL_NO_CONTEXT, ctx_attribs);
    eglMakeCurrent(display, surface, surface, context);
}

GLuint initShaderCode(const char *code, GLint type) {
    GLuint sh = glCreateShader(type);
    if (sh == 0) {
        LOGE("glCreateShader %d failed!", type);
        return 0;
    }
    //加载shader
    glShaderSource(sh, 1, &code, 0);
    glCompileShader(sh);
    GLint status;
    glGetShaderiv(sh, GL_COMPILE_STATUS, &status);
    if (status == 0) {
        LOGE("glCompileShader failed!");
        //output error log
        char log[1024] = {0};
        glGetShaderInfoLog(sh, sizeof(log), 0, log);
        LOGE("glCompileShader failed! %s", log);

        return 0;
    }
    LOGE("glCompileShader success!");
    return sh;
}

void initShader() {
    GLuint vsh = initShaderCode(vertexShader, GL_VERTEX_SHADER);
    GLuint fsh = initShaderCode(fragYUV420P, GL_FRAGMENT_SHADER);
    GLuint program = glCreateProgram();
    glAttachShader(program, vsh);
    glAttachShader(program, fsh);
    glLinkProgram(program);
    GLint status;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == 0) {
        LOGE("glLinkProgram failed!");
        return;
    }
    LOGE("glLinkProgram success!");
    glUseProgram(program);

    static float ver[] = {
            1.0f, -1.0f, 0.0f, //右下
            -1.0f, -1.0f, 0.0f, //左下
            1.0f, 1.0f, 0.0f, //右上
            -1.0f, 1.0f, 0.0f //左上
    };
    GLint apos = glGetAttribLocation(program, "aPosition");
    glEnableVertexAttribArray(apos);
    glVertexAttribPointer(apos, 3, GL_FLOAT, GL_FALSE, 12, ver);

    static float txt[] = {
            1.0f, 0.0f, //右下
            0.0f, 0.0f, //左下
            1.0f, 1.0f, //右上
            0.0f, 1.0f //左上
    };
    GLint atex = glGetAttribLocation(program, "aTexCoord");
    glEnableVertexAttribArray(atex);
    glVertexAttribPointer(atex, 2, GL_FLOAT, GL_FALSE, 8, txt);

    glUniform1i(glGetUniformLocation(program, "yTexture"), 0);
    glUniform1i(glGetUniformLocation(program, "uTexture"), 1);
    glUniform1i(glGetUniformLocation(program, "vTexture"), 2);
    //setting window size
    LOGE("success shader");


}

void createTexture(int index, int width, int height, uint8_t *buf) {
    if (textures[index] == 0) {
        glGenTextures(1, &textures[index]);
        glBindTexture(GL_TEXTURE_2D, textures[index]);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_LUMINANCE, width, height, 0, GL_LUMINANCE,
                     GL_UNSIGNED_BYTE,
                     0);
    }
    glActiveTexture(GL_TEXTURE0 + index);
    glBindTexture(GL_TEXTURE_2D, textures[index]);
    glTexSubImage2D(GL_TEXTURE_2D, 0, 0, 0, width, height, GL_LUMINANCE, GL_UNSIGNED_BYTE,
                    buf);
}


void drawData(int width, int height, unsigned char *buf[]) {
    if (display == 0) { //is only in this place init, can be successful to play
        initEGL(win);
        initShader();
        //get window size
        int w = ANativeWindow_getWidth(win);
        int h = ANativeWindow_getHeight(win);
        //place into the center of window
        double r = width / (double) height;
        int widthDisplay = w;
        int heightDisplay = w / r;
        if (heightDisplay > h) {
            heightDisplay = h;
            widthDisplay = h * r;
        }
        glClearColor(.2, .2, .2, 1.0);
        glClear(GL_COLOR_BUFFER_BIT);
        glViewport((w - widthDisplay) / 2, (h - heightDisplay) / 2, widthDisplay, heightDisplay);

    }
    createTexture(0, width, height, buf[0]);
    createTexture(1, width / 2, height / 2, buf[1]);
    createTexture(2, width / 2, height / 2, buf[2]);
    glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    eglSwapBuffers(display, surface);
    LOGE("drawData");

}

SLObjectItf playObj = nullptr;
SLObjectItf engineObject = nullptr;
SLEngineItf engineEngine = nullptr;
SLPlayItf playItf = nullptr;
SLObjectItf outputMixObject = nullptr;
SLAndroidSimpleBufferQueueItf pcmBufferQueue = nullptr;

using namespace std;

AVFormatContext *avFormatContext;
int64_t totalMs;
int64_t aPts = 0;
int64_t vPts = 0;

//involves structs including AVFrame, AVPacket, AVCodecContext
void openFile(std::string url) {
    avformat_open_input(&avFormatContext, url.c_str(), 0, 0);//open mp4 file
    avformat_find_stream_info(avFormatContext,
                              NULL);//get stream info such as video stream, audio stream info
    totalMs = avFormatContext->duration / (AV_TIME_BASE / 1000);
}


int videoStreamIndex;
int audioStreamIndex;
AVCodecParameters *v_para, *a_para;
AVCodecContext *v_codec, *a_codec;

void openDecoder() {

    // get video stream index
    videoStreamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_VIDEO, -1, -1, 0, 0);
    LOGE("audioStream index: %d", videoStreamIndex);
    v_para = avFormatContext->streams[videoStreamIndex]->codecpar;
    LOGE("v_para->codec_id: %d", v_para->codec_id);
    LOGE("v_para->width: %d", v_para->width);
    LOGE("v_para->height: %d", v_para->height);
    LOGE("v_para->format: %d", v_para->format);
    LOGE("v_para->bit_rate: %lld", v_para->bit_rate);
    AVCodec *cd = avcodec_find_decoder(v_para->codec_id);
    v_codec = avcodec_alloc_context3(cd);
    avcodec_parameters_to_context(v_codec, v_para);
    LOGE("v_codec context -> pix_fmt: %d", v_codec->pix_fmt);
    v_codec->thread_count = 8;
    avcodec_open2(v_codec, 0, 0);

    // get audio stream index
    audioStreamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, 0, 0);
    LOGE("audioStream index: %d", audioStreamIndex);
    a_para = avFormatContext->streams[audioStreamIndex]->codecpar;
    LOGE("a_para->codec_id: %d", a_para->codec_id);
    LOGE("a_para->sample_rate: %d", a_para->sample_rate);
    LOGE("a_para->channels: %d", a_para->channels);
    LOGE("a_para->format: %d", a_para->format);
    LOGE("a_para->bit_rate: %lld", a_para->bit_rate);
    AVCodec *ad = avcodec_find_decoder(a_para->codec_id);
    a_codec = avcodec_alloc_context3(ad);
    avcodec_parameters_to_context(a_codec, a_para);
    a_codec->thread_count = 8;
    avcodec_open2(a_codec, 0, 0);
}


double r2d(AVRational r) {
    return r.num == 0 || r.den == 0 ? 0. : (double) r.num / (double) r.den;
}

bool isRunning = true;
bool isExit[3] = {false};

struct PcmData {
    uint8_t *data;
    int size;
    int64_t pts;

public:
    void release() {
        if (data) {
            delete[] data;
            data = nullptr;
        }
    }
};

SwrContext *swrContext = nullptr;

void initSwr() { //audio resample
    swrContext = swr_alloc();
    swrContext = swr_alloc_set_opts(swrContext,
            //out
                                    av_get_default_channel_layout(2),
                                    AV_SAMPLE_FMT_S16,
                                    a_codec->sample_rate,
            //in
                                    av_get_default_channel_layout(a_codec->channels),
                                    a_codec->sample_fmt,
                                    a_codec->sample_rate,
                                    0, 0);
    swr_init(swrContext);
}

PcmData resampleAudio(AVFrame *audioFrame) {
    int pcmSize = (audioFrame->nb_samples * //一帧采了几个点，可以这样理解吗？答：可以
                   av_get_bytes_per_sample(AV_SAMPLE_FMT_S16) *//每个点占几个字节，可以这样理解吗？答：可以
                   audioFrame->channels);//声道数，可以这样理解吗？答：可以
    uint8_t *pcm_buffer = new uint8_t[pcmSize]; //how to release this memory? delete[] pcm_buffer
    uint8_t *out_buffer[2] = {0x00};
    out_buffer[0] = pcm_buffer;
    int len = swr_convert(swrContext,
                          out_buffer,
                          audioFrame->nb_samples,
                          (const uint8_t **) audioFrame->data,
                          audioFrame->nb_samples);
    LOGE("swr_convert len: %d", len);
    LOGE("pcmSize: %d", pcmSize);
    if (len <= 0) {
        return PcmData();
    }

    PcmData pcmData;
    pcmData.data = pcm_buffer;
    pcmData.size = pcmSize;
    pcmData.pts = audioFrame->pts;
    return pcmData;
}

queue<PcmData> pcmDatas;
mutex pcmDatasMutex;

PcmData getPcmData() {
    pcmDatasMutex.lock();

    while (pcmDatas.empty()) { //why use while instead of if?  //if is not safe
        pcmDatasMutex.unlock();
        av_usleep(10);
        continue;
    }

    PcmData pcmData = pcmDatas.front();
    pcmDatas.pop();
    pcmDatasMutex.unlock();
    return pcmData;
}

queue<AVPacket *> videoPackets, audioPackets;
mutex videoPacketsMutex, audioPacketsMutex;

AVPacket *readPacket() {
    AVPacket *packet = av_packet_alloc();
    int ret = av_read_frame(avFormatContext, packet);
    if (ret != 0) {
        LOGE("end of file");
        av_packet_free(&packet);
        isRunning = false;
        return 0;
    }
    packet->pts =
            packet->pts * (r2d(avFormatContext->streams[packet->stream_index]->time_base) * 1000);
    return packet;
}

void readPacketLoop() {
    LOGE("readPacketLoop");
    while (isRunning) {
        if (videoPackets.size() > 100 || audioPackets.size() > 100) {
            av_usleep(10);
            continue;
        }
        AVPacket *packet = readPacket();
        if (!packet) {
            break;
        }
        if (packet->stream_index == videoStreamIndex) {
            videoPacketsMutex.lock();
            videoPackets.push(packet);
            LOGE("videoPackets size: %d", videoPackets.size());
            videoPacketsMutex.unlock();
        } else if (packet->stream_index == audioStreamIndex) {
            audioPacketsMutex.lock();
            audioPackets.push(packet);
            LOGE("audioPackets size: %d", audioPackets.size());
            audioPacketsMutex.unlock();
        }
    }
    isExit[0] = true;
    LOGE("readPacketLoop end");
}


AVFrame *videoFrame = nullptr;

void decodeVideoPacket() {
    if (videoFrame == nullptr) {
        videoFrame = av_frame_alloc();
    }
    AVPacket *packet = videoPackets.front();
    videoPackets.pop();
    int re = avcodec_send_packet(v_codec, packet);
    av_packet_free(&packet);
    if (re != 0)
        return;
    re = avcodec_receive_frame(v_codec, videoFrame);
    if (re != 0) //not get frame, void data can make drawData 0x501 error
        return;
    vPts = videoFrame->pts;
    drawData(videoFrame->width, videoFrame->height, videoFrame->data);


}

uint8_t *cache_copy = nullptr;
void pcmCall(SLAndroidSimpleBufferQueueItf bf, void *contex) {
    PcmData d = getPcmData();
    if (d.data == nullptr) {
        return;
    }
    if (cache_copy == nullptr) {
        cache_copy = new uint8_t[d.size];
    }
    memcpy(cache_copy, d.data, d.size);
    aPts = d.pts;
    (*pcmBufferQueue)->Enqueue(pcmBufferQueue, cache_copy, d.size);
    d.release();
}

AVFrame *audioFrame = nullptr;

void decodeAudioPacket() {
    if (audioFrame == nullptr) {
        audioFrame = av_frame_alloc();
    }
    AVPacket *packet = audioPackets.front();
    audioPackets.pop();
    int re = avcodec_send_packet(a_codec, packet);
    av_packet_free(&packet);
    if (re != 0)
        return;
    while (true){
        int ret = avcodec_receive_frame(a_codec, audioFrame);
        if (ret != 0) {
            break;
        }
        PcmData pcmData = resampleAudio(audioFrame);
        if (pcmData.data == nullptr) {
            continue;
        }
        while (true) {
            pcmDatasMutex.lock();
            if (pcmDatas.size() > 100) {
                pcmDatasMutex.unlock();
                av_usleep(10);
                continue;
            }
            pcmDatas.push(pcmData);
            LOGE("pcmDatas size: %d", pcmDatas.size());
            pcmDatasMutex.unlock();
            break;
        }

    }
}

void decodeVideoLoop() {
    while (isRunning) {
        LOGE("index vPts %lld, aPts %lld", vPts, aPts);
        if (vPts - aPts > 0 && vPts > 0 && aPts > 0) {
            av_usleep(10);
            continue;
        }
        if (videoPackets.size() < 4) {
            av_usleep(10);
            continue;
        }
        decodeVideoPacket();
    }
    isExit[1] = true;
    LOGE("decodeVideoLoop end");
}

void decodeAudioLoop() {
    while (isRunning) {
        if (audioPackets.size() < 4) {
            av_usleep(10);
            continue;
        }
        decodeAudioPacket();
    }
    isExit[2] = true;
    LOGE("decodeAudioLoop end");
}




void initSles() {
    slCreateEngine(&engineObject, 0, 0, 0, 0, 0);
    (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE,
                                  &engineEngine);//1.get engine interface
    (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 0, 0, 0);
    (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSink = {&outputMix, 0};
    SLDataLocator_AndroidSimpleBufferQueue androidQueue = {
            SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
            2};
    SLDataFormat_PCM pcm = {SL_DATAFORMAT_PCM,
                            2,
                            SL_SAMPLINGRATE_44_1,
                            SL_PCMSAMPLEFORMAT_FIXED_16,
                            SL_PCMSAMPLEFORMAT_FIXED_16,
                            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,
                            SL_BYTEORDER_LITTLEENDIAN};
    SLDataSource slDataSource = {&androidQueue, &pcm};
    const SLInterfaceID ids[] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[] = {SL_BOOLEAN_TRUE};
    (*engineEngine)->CreateAudioPlayer(engineEngine, &playObj, &slDataSource, &audioSink, 1,
                                       ids, req);
    (*playObj)->Realize(playObj, SL_BOOLEAN_FALSE);
    (*playObj)->GetInterface(playObj, SL_IID_PLAY, &playItf);

    (*playObj)->GetInterface(playObj, SL_IID_BUFFERQUEUE, &pcmBufferQueue);
    (*pcmBufferQueue)->RegisterCallback(pcmBufferQueue, pcmCall, nullptr);
    (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PLAYING);
    (*pcmBufferQueue)->Enqueue(pcmBufferQueue, "", 1);

}



extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1player_JniImp_setSurface(JNIEnv *env, jclass clazz, jobject surface) {
    win = ANativeWindow_fromSurface(env, surface);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1player_JniImp_playVideo(JNIEnv *env, jclass clazz, jstring path) {
    // TODO: implement playVideo()
    isRunning = true;
    isExit[0] = false;
    isExit[1] = false;
    isExit[2] = false;
    const char *path_ = env->GetStringUTFChars(path, 0);
    av_register_all();
    avcodec_register_all();
    avformat_network_init();
    openFile(path_);
    LOGE("totalMs: %lld", totalMs);
    openDecoder();
    initSwr();

    thread readPacket(readPacketLoop);
    readPacket.detach();
    thread decodeVideo(decodeVideoLoop);
    decodeVideo.detach();
    thread decodeAudio(decodeAudioLoop);
    decodeAudio.detach();

    initSles();

    env->ReleaseStringUTFChars(path, path_);
}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1player_JniImp_stopVideo(JNIEnv *env, jclass clazz) {
    // TODO: implement stopVideo()
    isRunning = false;
    while (!isExit[0] || !isExit[1] || !isExit[2]) {
        av_usleep(10);
    }
    (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_STOPPED);
    //music stop
    //release queue
    while (!videoPackets.empty()) {
        AVPacket *pkt = videoPackets.front();
        videoPackets.pop();
        av_packet_free(&pkt);
    }
    while (!audioPackets.empty()) {
        AVPacket *pkt = audioPackets.front();
        audioPackets.pop();
        av_packet_free(&pkt);
    }
    while (!pcmDatas.empty()) {
        PcmData d = pcmDatas.front();
        pcmDatas.pop();
        d.release();
    }

    vPts = 0;
    aPts = 0;

    //release frame
    av_frame_free(&videoFrame);
    av_frame_free(&audioFrame);
    //release codec
    avcodec_close(v_codec);
    avcodec_free_context(&v_codec);
    avcodec_close(a_codec);
    avcodec_free_context(&a_codec);
    //release format
    avformat_close_input(&avFormatContext);
    //release swr
    swr_free(&swrContext);
    //release seles
    (*playObj)->Destroy(playObj);
    (*outputMixObject)->Destroy(outputMixObject);
    (*engineObject)->Destroy(engineObject);
    //release cache
    delete cache_copy;
    cache_copy = nullptr;
    //release egl
    eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroySurface(display, surface);
    eglTerminate(display);
    display = EGL_NO_DISPLAY;
    surface = EGL_NO_SURFACE;
    ANativeWindow_release(win);
    win = 0;
    //release texture
    for (int i = 0; i < 3; ++i) {
        glDeleteTextures(1, &textures[i]);
        textures[i] = 0;
    }
    LOGE("stopVideo end");

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1player_JniImp_pauseVideo(JNIEnv *env, jclass clazz) {
    // TODO: implement pauseVideo()
    (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PAUSED);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1player_JniImp_resumeVideo(JNIEnv *env, jclass clazz) {
    (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PLAYING);

}
