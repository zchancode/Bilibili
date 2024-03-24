package com.example.zchan_ffrtmp_plus;

public class JniImp {

    // Used to load the 'zchan_ffrtmp_plus' library on application startup.
    static {
        System.loadLibrary("zchan_ffrtmp_plus");
    }

    public static native void init(String url, int width, int height);

    public static native void pushNV12(byte[] y, byte[] uv);
    public static native void pushI420(byte[] y, byte[] u, byte[] v);
    public static native void pushPCM(byte[] data);

    public static native void startFRtmp();

    public static native void stopFRtmp();

}