package com.example.zchan_orgrtmp;

public class JniImp {

    // Used to load the 'zchan_orgrtmp' library on application startup.
    static {
        System.loadLibrary("zchan_orgrtmp");
    }
    public static native String stringFromJNI();
}