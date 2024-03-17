package com.example.zchan_orgrtmp;

public class JniImp {

    // Used to load the 'zchan_orgrtmp' library on application startup.
    static {
        System.loadLibrary("zchan_orgrtmp");
    }
    public static native void initFaac();
    public static native void initX264(int width,int height,int fps,int bitrate);
    public static native void startLive(String url);
    public static native void stopLive();
    public static native void pushAudio(byte[] data, int len);
    public static native void pushVideo(byte[] ydata,byte[] uvdata, int width, int height);
}