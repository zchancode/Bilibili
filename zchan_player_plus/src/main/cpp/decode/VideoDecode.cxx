//
// Created by Administrator on 2024-03-17.
//
#include <IObserver.cxx>
#include "android/log.h"
#include <queue>
#include "thread"
#include <AudioPlayer.cxx>

extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/time.h"
}
#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_structure",FORMAT,##__VA_ARGS__)

class VideoDecode : public IObserver {
private:
    int videoStreamIndex;
    AVCodecParameters *v_para;
    AVCodecContext *v_codec;
    std::queue<Data> videoPackets;
    std::mutex mutex;
    AVFrame *frame = nullptr;
    bool isRunning = true;
    bool isExit = false;
    AudioPlayer *audioPlayer = nullptr;
    int64_t vPts = 0;

    void threadMain() {
        while (isRunning) {
            if (videoPackets.empty()) {
                av_usleep(10);
                continue;
            }

            LOGE("audio pts is %lld, video pts is %lld", audioPlayer->getAPts(), vPts);
            //这里要注意lld不能是d 不然输出的数据会有问题
            if (audioPlayer->getAPts() < vPts && vPts > 0) {
                av_usleep(1);
                continue;
            }

            mutex.lock();
            Data data = videoPackets.front();
            videoPackets.pop();
            mutex.unlock();
            AVPacket *packet = (AVPacket *) data.data;
            avcodec_send_packet(v_codec, packet);
            avcodec_receive_frame(v_codec, frame);
            Data videoFrame;
            videoFrame.data = frame;
            this->vPts = frame->pts;
            sendData(videoFrame);
            av_packet_free(&packet);
        }
        isExit = true;
    }

public:
    VideoDecode(AVFormatContext *avFormatContext, AudioPlayer *audioPlayer) {
        isRunning = true;
        isExit = false;
        this->audioPlayer = audioPlayer;
        this->frame = av_frame_alloc();
        videoStreamIndex = av_find_best_stream(avFormatContext, AVMEDIA_TYPE_VIDEO, -1, -1, 0, 0);
        v_para = avFormatContext->streams[videoStreamIndex]->codecpar;
        AVCodec *cd = avcodec_find_decoder(v_para->codec_id);
        v_codec = avcodec_alloc_context3(cd);
        avcodec_parameters_to_context(v_codec, v_para);
        v_codec->thread_count = 8;
        avcodec_open2(v_codec, 0, 0);

    }

    ~VideoDecode() {
        isRunning = false;
        while (!isExit) {
            av_usleep(10);
        }
        avcodec_close(v_codec);
        avcodec_free_context(&v_codec);
        av_frame_free(&frame);

        while (!videoPackets.empty()) {
            Data data = videoPackets.front();
            videoPackets.pop();
            av_packet_free((AVPacket **) &data.data);
        }
        vPts = 0;
        LOGE("VideoDecode release");
    }

    void start() {
        std::thread t(&VideoDecode::threadMain, this);
        t.detach();
    }


    void receiveData(Data data) override {
        while (videoPackets.size() > 100) { //block the thread
            av_usleep(10);
        }
        AVPacket *packet = (AVPacket *) data.data;
        if (packet->stream_index == videoStreamIndex && packet->data != nullptr) {
            mutex.lock();
            videoPackets.push(data);
            mutex.unlock();
        }

    }

};