#include <jni.h>
#include <android/log.h>
#include <android/native_window_jni.h>
#include <EGL/egl.h>
#include <GLES3/gl3.h>
#include <malloc.h>
//c++ string
#include <string>
#include <math.h>
#include <unistd.h>

extern "C" {
#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"
}


using namespace std;
#define TAG "zchan_opengl"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__)

string vshaderLoader() {
    string shader;
    shader.append("#version 300 es\n"
                  "in vec3 aPos;\n"
                  "in vec2 inTexPos;\n"
                  "out vec2 outTexPos;\n"
                  "uniform vec4 inColor;\n"
                  "out vec4 outColor;\n"
                  "uniform mat4 model;\n"
                  "void main() {\n"
                  "  gl_Position = model * vec4(aPos, 1.0);\n"
                  "  outTexPos = inTexPos;\n"
                  "  outColor = inColor;\n"
                  "}");
    return shader;
}

string fshaderLoader() {
    string shader;
    shader.append("#version 300 es\n"
                  "precision mediump float;\n"
                  "in vec2 outTexPos;\n"
                  "out vec4 FragColor;\n"
                  "uniform sampler2D texture0;\n"
                  "in vec4 outColor;\n"
                  "void main() {\n"
                  "    FragColor = texture(texture0, outTexPos);\n"
                  "}");
    return shader;

}

EGLSurface surface = EGL_NO_SURFACE;
EGLDisplay display = EGL_NO_DISPLAY;

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
    glShaderSource(sh, 1, &code, 0);
    glCompileShader(sh);
    GLint status;
    glGetShaderiv(sh, GL_COMPILE_STATUS, &status);
    if (status == 0) {
        LOGE("glCompileShader failed!");
        //output error log
        GLint logLen;
        glGetShaderiv(sh, GL_INFO_LOG_LENGTH, &logLen);
        if (logLen > 0) {
            char *log = (char *) malloc(logLen);
            glGetShaderInfoLog(sh, logLen, NULL, log);
            LOGE("shader compile error: %s", log);
            free(log);
        }
        return 0;
    }
    LOGE("glCompileShader success!");
    return sh;
}

ANativeWindow *win = 0;


GLuint program;

void buildShader() {
    GLuint vsh = initShaderCode(vshaderLoader().data(), GL_VERTEX_SHADER);
    GLuint fsh = initShaderCode(fshaderLoader().data(), GL_FRAGMENT_SHADER);
    program = glCreateProgram();
    glAttachShader(program, vsh);
    glAttachShader(program, fsh);
    glLinkProgram(program);
    GLint status;
    glGetProgramiv(program, GL_LINK_STATUS, &status);
    if (status == 0) {
        LOGE("glLinkProgram failed!");
        //output error log
        GLint logLen;
        glGetProgramiv(program, GL_INFO_LOG_LENGTH, &logLen);
        if (logLen > 0) {
            char *log = (char *) malloc(logLen);
            glGetProgramInfoLog(program, logLen, NULL, log);
            LOGE("program link error: %s", log);
            free(log);
        }
        return;
    }

    glDeleteShader(vsh);
    glDeleteShader(fsh);
    glUseProgram(program);
}
GLuint texture;
void drawData() {
    initEGL(win);
    buildShader();

    static float ver[] = {
            1.0f, -1.0f, 0.0f, //右下
            -1.0f, -1.0f, 0.0f, //左下
            1.0f, 1.0f, 0.0f, //右上
            -1.0f, 1.0f, 0.0f //左上
    };
    GLint apos = glGetAttribLocation(program, "aPos");
    glEnableVertexAttribArray(apos);
    glVertexAttribPointer(apos, 3, GL_FLOAT, GL_FALSE, 12, ver);

    static float txt[] = {
            1.0f, 1.0f, //右下
            0.0f, 1.0f, //左下
            1.0f, 0.0f, //右上
            0.0f, 0.0f //左上
    };
    GLint atex = glGetAttribLocation(program, "inTexPos");
    glEnableVertexAttribArray(atex);
    glVertexAttribPointer(atex, 2, GL_FLOAT, GL_FALSE, 8, txt);


    glUniform4f(glGetUniformLocation(program, "inColor"), 1.0f, 0.0f, 0.0f, 1.0f);
    glUniform1i(glGetUniformLocation(program, "texture0"), 0);


    int width, height, nrChannels;
    unsigned char *data = stbi_load("/sdcard/a.png", &width, &height, &nrChannels, 0);

    LOGE("width: %d, height: %d", width, height);

    glGenTextures(1, &texture);
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, GL_RGB, GL_UNSIGNED_BYTE, data);
    glGenerateMipmap(GL_TEXTURE_2D);
    stbi_image_free(data);

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
    glViewport((w - widthDisplay) / 2, (h - heightDisplay) / 2, widthDisplay, heightDisplay);


    for (int i = 0; i < 10000; ++i) {
        glClear(GL_COLOR_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        float mat[] = {
                (float) cos(i * 0.1), (float) -sin(i * 0.1), 0.0f, 0.0f,
                (float) sin(i * 0.1), (float) cos(i * 0.1), 0.0f, 0.0f,
                0.0f, 0.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };
        glUniformMatrix4fv(glGetUniformLocation(program, "model"), 1, GL_FALSE, mat);
        glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
        eglSwapBuffers(display, surface);
    }

}

void release() {
    //release egl
    eglMakeCurrent(display, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroySurface(display, surface);
    eglTerminate(display);
    display = EGL_NO_DISPLAY;
    surface = EGL_NO_SURFACE;
    //release window
    ANativeWindow_release(win);
    win = 0;

    //release program
    glDeleteProgram(program);
    //release texture
    glDeleteTextures(1, &texture);


}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1opengl_GLImp_setSurface(JNIEnv *env, jclass clazz, jobject surface) {
    win = ANativeWindow_fromSurface(env, surface);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1opengl_GLImp_playSurface(JNIEnv *env, jclass clazz, jstring url) {
    const char *path = env->GetStringUTFChars(url, 0);
    drawData();
    env->ReleaseStringUTFChars(url, path);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_example_zchan_1opengl_GLImp_closeSurface(JNIEnv *env, jclass clazz) {
    release();
}