package com.example.zchan_structure;

public class JniImp {

    // Used to load the 'zchan_structure' library on application startup.
    static {
        System.loadLibrary("zchan_structure");
    }

    public static native void setSurface(Object surface);

    public static native void pushAudio(byte[] data);

    public static native void pushVideo(byte[] y, byte[] uv);

    public static native void startPlay(String url);
}