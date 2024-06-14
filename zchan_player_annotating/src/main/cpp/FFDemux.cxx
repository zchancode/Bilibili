#pragma once
extern "C" {
#include "libavformat/avformat.h"
#include "libavutil/time.h"
}

#include "FFObserver.cxx"
#include <thread>

#include <android/log.h>

#define LOGE(FORMAT, ...) __android_log_print(ANDROID_LOG_ERROR,"zchan_player_annotating",FORMAT,##__VA_ARGS__)

#include <string>

class FFDemux : public FFObserver {
private:
    double r2d(AVRational r) {
        return r.num == 0 || r.den == 0 ? 0. : (double) r.num / (double) r.den;
    }

    AVFormatContext *avFormatContext = nullptr;
    int64_t totalMs = 0;
    bool isRunning = true;
    bool isExit = false;

    void mainThread() {
        while (isRunning) {
            AVPacket *packet = av_packet_alloc();
            int re = av_read_frame(avFormatContext, packet);
            //read frame 会给packet创建buffer，需要调用av_packet_free释放
            if (re != 0) { //read frame failed or end of file
                av_packet_free(&packet);
                //free packet when read frame failed or end of file
                isExit = true;
                break;
            }
            packet->pts = packet->pts *
                          (r2d(avFormatContext->streams[packet->stream_index]->time_base) * 1000);
            FFData d;
            d.p[0] = packet;
            d.dataType = packet->stream_index;
            send(d);
        }
        isExit = true;
    }

public:
    ~FFDemux() {
        isRunning = false;
        while (!isExit) {
            av_usleep(1);
            LOGE("wait for FFDemux");
        }
        LOGE("~FFDemux()");
        avformat_close_input(&avFormatContext);
        avformat_free_context(avFormatContext);
        isRunning = true;
        isExit = false;
    }

    FFDemux(std::string url) {
        av_register_all();
        //注册封装器 register all muxers and demuxers
        avcodec_register_all();
        //注册解封装器 register all codecs, parsers and bitstream filters
        avformat_network_init();
        //注册网络 register network
        avformat_open_input(&avFormatContext, url.c_str(), 0, 0);//open mp4 file
        //内部会调用avformat_alloc_context()分配内存，所以这里传NULL就可以了
        //这个avFormatContext要取地址 the address of the pointer to the AVFormatContext needed to be set to NULL
        avformat_find_stream_info(avFormatContext, NULL);
        //get stream info such as video stream, audio stream info
        //对于MP4可以不要这句就可以获取到文件信息 for MP4, you can get file info without this sentence
        //对于有些格式不掉用这句就获取不到文件信息 but for some formats, you can't get file info without this sentence
        totalMs = avFormatContext->duration / (AV_TIME_BASE / 1000);
        //得到时间份数duration后转换为毫秒 get the time base and convert it to milliseconds
        //这个值不一定有duration，有些文件开头没有这个值，要读到Stream才有这个值
        //this value is not necessarily the duration, some files don't have this value at the beginning, you need to read the stream to get this value
        //你把这个作为分母要注意分母不能为0 if you use this as the denominator, make sure it's not 0
        LOGE("totalMs is %lld", totalMs);
    }

    AVFormatContext *getAVFormatContext() {
        return avFormatContext;
    }

    void start() {
        std::thread th(&FFDemux::mainThread, this);
        th.detach();
    }
};
/*
 * 为什么巨大的原始视频可以编码成很小的视频呢?这其中的技术是什么呢?
 *  H264 通过GOP I帧 P帧 B帧的方式进行推导
 *
 * 怎么做到直播秒开优化？
 *  I帧间隔减小
 *  avformat_find_stream_info占用时间过长，这个函数通过读取码流推导出流的信息，里面可以调整参数
 *
 *  AAC和PCM的区别？
 *  AAC就是给PCM数据开始时候加采样率，声道数，采样点的大小
 *
 *  H264有两种存储方式？
 *  只知道Sps pps gop被0001或者001分割
 *
 *  在MPEG标准中图像类型有哪些？
 *  I帧 P帧 B帧
 *
 *  如何解决卡顿？
 *  拉流和解码两个线程，搞一个队列
 *
 *
 */
