package com.example.zchan_hardrtmp;

public class JniImp {

    // Used to load the 'zchan_hardrtmp' library on application startup.
    static {
        System.loadLibrary("zchan_hardrtmp");
    }

    public native String stringFromJNI();
}