package com.example.zchan_hardrtmp;

public class JniImp {

    // Used to load the 'zchan_hardrtmp' library on application startup.
    static {
        System.loadLibrary("zchan_hardrtmp");
    }

    public static native void init(String url);
    public static native void pushH264(byte[] data, int len, long tms);
    public static native void pushAAC(byte[] data, int len, long tms);
}