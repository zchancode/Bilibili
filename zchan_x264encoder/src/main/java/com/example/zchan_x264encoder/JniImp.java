package com.example.zchan_x264encoder;

public class JniImp {

    // Used to load the 'zchan_x264encoder' library on application startup.
    static {
        System.loadLibrary("zchan_x264encoder");
    }

    public static native void initX264(int width, int height, int fps, int bite);

    public static native void pushI420(byte[] y, byte[] u, byte[] v);
    public static native void pushNV12(byte[] y, byte[] uv);

    public static native void releaseX264();
}