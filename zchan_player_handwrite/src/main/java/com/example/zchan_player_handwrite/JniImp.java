package com.example.zchan_player_handwrite;

public class JniImp {

    // Used to load the 'zchan_player_handwrite' library on application startup.
    static {
        System.loadLibrary("zchan_player_handwrite");
    }
    public static native void init();
}