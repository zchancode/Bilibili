package com.example.zchan_structure;

public class JniImp {

    // Used to load the 'zchan_structure' library on application startup.
    static {
        System.loadLibrary("zchan_structure");
    }

    public static native String stringFromJNI();
}