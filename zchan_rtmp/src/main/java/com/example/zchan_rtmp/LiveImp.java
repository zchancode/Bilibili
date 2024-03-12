package com.example.zchan_rtmp;

/**
 * Created by Mr.Chan
 * Time 2024-01-31
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class LiveImp {
    static {
        System.loadLibrary("zchan_rtmp");
    }

    public static native void pushVideo(long mat_addr);


    public static native void pushVideoYUV420NV12(byte[] y, byte[] uv, int width, int height);

    public static native void pushAudio(byte[] data, int len);

    public static native void init(int width, int height,String url);

    public static native void stopPush();

    public static native void startPush();
}
