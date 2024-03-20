#pragma once

#include <libfaac/faac.h>
#include <sys/types.h>
#include <malloc.h>

class FaacEncoder {
private:
    u_long inputSamples;
    faacEncHandle audioCodec = 0;
    u_long maxOutputBytes;
public:
    FaacEncoder(u_long sampleRate, u_long channels) {
        audioCodec = faacEncOpen(sampleRate, channels, &inputSamples, &maxOutputBytes);
        faacEncConfigurationPtr config = faacEncGetCurrentConfiguration(audioCodec);
        config->mpegVersion = MPEG4;
        config->aacObjectType = LOW;
        config->inputFormat = FAAC_INPUT_16BIT;
        config->outputFormat = 1;
        faacEncSetConfiguration(audioCodec, config);
    }

    ~FaacEncoder() {
        faacEncClose(audioCodec);
    }

    int encode(int8_t *in, int8_t **out) {
        *out = (int8_t *) malloc(maxOutputBytes);
        int size = faacEncEncode(audioCodec, (int32_t *) in, inputSamples,
                                 (uint8_t *) *out, maxOutputBytes);

        return size;
    }
};
