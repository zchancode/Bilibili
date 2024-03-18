//
// Created by Administrator on 2024-03-18.
//
#pragma once

#include <GLES3/gl3.h>
#include <android/log.h>
#define TAG "zchan_structure"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#include <EGL/egl.h>

class Shader {
private:
    const char *vertexShader = "#version 300 es\n"
                                      "in vec4 aPosition;\n"
                                      "in vec2 aTexCoord;\n"
                                      "out vec2 vTexCoord;\n"
                                      "void main() {\n"
                                      "vTexCoord = vec2(aTexCoord.x, 1.0 - aTexCoord.y);\n"
                                      "   gl_Position = aPosition;\n"
                                      "}";

    const char *fragYUV420P = "#version 300 es\n"
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
    GLuint textures[3] = {0};

    GLuint initShaderCode(const char *code, GLint type) {
        GLuint sh = glCreateShader(type);
        if (sh == 0) {
            LOGE("glCreateShader %d failed!", type);
            return 0;
        }
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


public:
    Shader(){
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

    ~Shader() {
        //release texture
        for (int i = 0; i < 3; ++i) {
            glDeleteTextures(1, &textures[i]);
            textures[i] = 0;
        }

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

    void draw() {
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
    }

};