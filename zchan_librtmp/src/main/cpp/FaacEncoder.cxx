#pragma once

#include <libfaac/faac.h>
#include <sys/types.h>
#include <malloc.h>
#include "SafeQueue.cxx"
#include "LibRtmp.cxx"
class FaacEncode {
private:
    u_long inputSamples;
    faacEncHandle audioCodec = 0;
    u_long maxOutputBytes;

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

public:
    FaacEncode(u_long sampleRate, u_long channels) {
        audioCodec = faacEncOpen(sampleRate, channels, &inputSamples, &maxOutputBytes);
        faacEncConfigurationPtr config = faacEncGetCurrentConfiguration(audioCodec);
        config->mpegVersion = MPEG4;
        config->aacObjectType = LOW;
        config->inputFormat = FAAC_INPUT_16BIT;
        config->outputFormat = 1;
        faacEncSetConfiguration(audioCodec, config);
    }

    ~FaacEncode() {
        faacEncClose(audioCodec);
    }

    void encode(int8_t *in) {
        int8_t *out = (int8_t *) malloc(maxOutputBytes);
        int size = faacEncEncode(audioCodec, (int32_t *) in, inputSamples,
                                 (uint8_t *) out, maxOutputBytes);
        RTMPPacket *audioPacket = createAudioPacket(out, size, av_gettime()/1000);
        queue->pushPacket(audioPacket);
    }
};
