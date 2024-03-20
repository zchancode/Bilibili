package com.example.zchan_faacencoder;

public class JniImp {

    // Used to load the 'zchan_faacencoder' library on application startup.
    static {
        System.loadLibrary("zchan_faacencoder");
    }

    public static native void initEncoder(int sampleRate, int channels);

    public static native void pushPCM(byte[] buffer);

    public static native void stopEncoder();
}