package com.example.zchan_librtmp;

public class JniImp {

    // Used to load the 'zchan_librtmp' library on application startup.
    static {
        System.loadLibrary("zchan_librtmp");
    }
    public static native void initX264(int width, int height, int fps, int bite);
    public static native void startRTMP();

    public static native void pushI420(byte[] y, byte[] u, byte[] v);
    public static native void pushNV12(byte[] y, byte[] uv);

    public static native void release();

    public static native void initFaac(int sampleRate, int channels);
    public static native void pushPCM(byte[] buffer);
}