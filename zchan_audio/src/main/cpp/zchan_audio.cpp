#include <jni.h>
#include <string>
#include <android/log.h>
#include <queue>
#include <mutex>
#include <thread>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_audio",FORMAT,##__VA_ARGS__);

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

using namespace std;

AVFormatContext *avFormatContext = nullptr;
int64_t totalMs = 0;

int audioStream = -1;
AVCodecParameters *a_para = nullptr;
AVCodecContext *a_codec = nullptr;
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

//involves structs including AVFrame, AVPacket, AVCodecContext
void openFile(std::string url) {
    avformat_open_input(&avFormatContext, url.c_str(), 0, 0);//open mp4 file
    /*
     * parameters:
     * 1. AVFormatContext **ps: pointer to a pointer to AVFormatContext it can be null
     * 2. const char *url: file path
     * 3. AVInputFormat *fmt: input format, if it is null, FFmpeg will try to guess the format
     * 4. AVDictionary **options: options for the demuxer
     */

    avformat_find_stream_info(avFormatContext,
                              NULL);//get stream info such as video stream, audio stream info
    /*
     * parameters:
     * 1. AVFormatContext *ic: pointer to AVFormatContext
     * 2. AVDictionary **options: options for the demuxer
     */

    totalMs = avFormatContext->duration / (AV_TIME_BASE / 1000);
    /*
     * AVCodecContext: codec context
     *
     * char *filename: file path
     * unsigned int nb_samples: the size of streams
     * AVStream **streams: Array of pointers to the streams
     * int64_t duration: per second has how many ticks
     * int64_t bit_rate: bit rate
     */
}


void openDecoder() {
    audioStream = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_AUDIO, -1, -1, 0, 0);
    LOGE("audioStream index: %d", audioStream);
    a_para = avFormatContext->streams[audioStream]->codecpar;
    /*
     * AVCodecParameters
     * enum AVCodecID codec_id: codec id
     * enum AVCodecID codec_type: codec type
     * int width: width
     * int height: height
     * AVRational sample_aspect_ratio: sample aspect ratio
     * int video_delay: video delay
     * int channels: channels
     * int sample_rate: sample rate
     * enum AVSampleFormat format: sample format
     * int64_t channel_layout: channel layout
     *
     */
    LOGE("Stream info:")
    LOGE("codec_id: %d", a_para->codec_id);
    LOGE("codec_type: %d", a_para->codec_type);
    LOGE("width: %d", a_para->width);
    LOGE("height: %d", a_para->height);
    LOGE("channels: %d", a_para->channels);
    LOGE("sample_rate: %d", a_para->sample_rate);
    LOGE("format: %d", a_para->format);
    LOGE("channel_layout: %d", a_para->channel_layout);

    AVCodec *cd = avcodec_find_decoder(a_para->codec_id);
    a_codec = avcodec_alloc_context3(cd);
    avcodec_parameters_to_context(a_codec, a_para);
    a_codec->thread_count = 8;
    avcodec_open2(a_codec, 0, 0);
}

double r2d(AVRational r) {
    return r.num == 0 || r.den == 0 ? 0. : (double) r.num / (double) r.den;
}

queue<AVPacket *> audioPackets;
mutex audioPacketsMutex;
bool isRunning = true;
bool isExit[2] = {false};

struct PcmData {
    uint8_t *data;
    int size;
    int64_t pts;

public:
    void clear() {
        if (data) {
            delete data;
            data = nullptr;
        }
    }
};

queue<PcmData> pcmDatas;
mutex pcmDatasMutex;
SLAndroidSimpleBufferQueueItf pcmBufferQueue = nullptr;

PcmData resampleAudio(AVFrame *audioFrame) {
    int pcmSize = (audioFrame->nb_samples * //一帧采了几个点，可以这样理解吗？答：可以
                   av_get_bytes_per_sample(AV_SAMPLE_FMT_S16) *//每个点占几个字节，可以这样理解吗？答：可以
                   audioFrame->channels);//声道数，可以这样理解吗？答：可以
    uint8_t *pcm_buffer = new uint8_t[pcmSize];
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

AVFrame *frame = nullptr;

void decodePacket() {
    if (frame == nullptr) {
        frame = av_frame_alloc();
    }
    AVPacket *packet = nullptr;
    packet = audioPackets.front();
    audioPackets.pop();
    avcodec_send_packet(a_codec, packet);
    av_packet_free(&packet);
    while (true) {
        int ret = avcodec_receive_frame(a_codec, frame);
        if (ret != 0) {
            break;
        }
        PcmData pcmData = resampleAudio(frame);
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

void decodePacketLoop() {
    LOGE("decodePacketLoop")
    while (isRunning) {
        audioPacketsMutex.lock();
        if (audioPackets.empty()) {
            audioPacketsMutex.unlock();
            av_usleep(10);
            continue;
        }
        decodePacket();
        audioPacketsMutex.unlock();
    }
    isExit[0] = true;
    LOGE("decodePacketLoop end")
}

void readPacket() {
    AVPacket *packet = av_packet_alloc();
    int ret = av_read_frame(avFormatContext, packet);
    if (ret != 0) {
        LOGE("end of file");
        av_packet_free(&packet);
        isRunning = false;
        return;
    }
    packet->pts =
            packet->pts * (r2d(avFormatContext->streams[packet->stream_index]->time_base) * 1000);
    LOGE("packet pts: %lld", packet->pts);
    audioPackets.push(packet);
}

void readPacketLoop() {
    LOGE("readPacketLoop")
    while (isRunning) {
        audioPacketsMutex.lock();
        if (audioPackets.size() < 100) {
            readPacket();
        }
        audioPacketsMutex.unlock();
        av_usleep(10);
    }
    isExit[1] = true;
    LOGE("readPacketLoop end")
}

SLObjectItf playObj = nullptr;
SLObjectItf engineObject = nullptr;
SLEngineItf engineEngine = nullptr;
SLPlayItf playItf = nullptr;
SLObjectItf outputMixObject = nullptr;


PcmData getPcmData() {
    pcmDatasMutex.lock();
    if (pcmDatas.empty()) {
        pcmDatasMutex.unlock();
        av_usleep(10);
        return PcmData();
    }
    PcmData pcmData = pcmDatas.front();
    pcmDatas.pop();
    pcmDatasMutex.unlock();
    return pcmData;
}

uint8_t *cache_copy = nullptr;
void pcmCall(SLAndroidSimpleBufferQueueItf bf, void *contex) {

    PcmData d = getPcmData();
    if (cache_copy == nullptr) {
        cache_copy = new uint8_t[d.size];
    }
    memcpy(cache_copy, d.data, d.size);
    (*pcmBufferQueue)->Enqueue(pcmBufferQueue, d.data, d.size);
    d.clear();
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
Java_com_example_zchan_1audio_ui_AudioActivity_playAudio(JNIEnv *env, jobject thiz, jstring path) {
    // TODO: implement playAudio()
    const char *input = env->GetStringUTFChars(path, 0);
    av_register_all();
    avcodec_register_all();
    avformat_network_init();
    openFile(input);
    openDecoder();
    initSwr();
    isRunning = true;
    isExit[0] = false;
    isExit[1] = false;

    LOGE("totalMs: %lld", totalMs);
    thread readPacket(readPacketLoop);
    readPacket.detach();

    thread decodePacket(decodePacketLoop);
    decodePacket.detach();

    initSles();

    env->ReleaseStringUTFChars(path, input);

}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1audio_ui_AudioActivity_stopAudio(JNIEnv *env, jobject thiz) {
    isRunning = false;
    (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PLAYING);
    while (!isExit[0] && !isExit[1]) {
        av_usleep(100);
    }
    (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_STOPPED);
    (*pcmBufferQueue)->Clear(pcmBufferQueue);
    (*playObj)->Destroy(playObj);
    (*outputMixObject)->Destroy(outputMixObject);
    (*engineObject)->Destroy(engineObject);

    avcodec_close(a_codec);
    avcodec_free_context(&a_codec);

    avformat_close_input(&avFormatContext);
    avformat_free_context(avFormatContext);

    swr_free(&swrContext);
    av_frame_free(&frame);

    free(cache_copy);
    cache_copy = nullptr;

    while (!pcmDatas.empty()) {
        PcmData d = pcmDatas.front();
        pcmDatas.pop();
        d.clear();
    }
    while (!audioPackets.empty()) {
        AVPacket *pkt = audioPackets.front();
        audioPackets.pop();
        av_packet_free(&pkt);
    }


}
extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1audio_ui_AudioActivity_pauseAudio(JNIEnv *env, jobject thiz) {
    SLuint32 state;
    (*playItf)->GetPlayState(playItf, &state);
    if (state == SL_PLAYSTATE_PLAYING)
        (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PAUSED);
    else
        (*playItf)->SetPlayState(playItf, SL_PLAYSTATE_PLAYING);
}