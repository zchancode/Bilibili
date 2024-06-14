package com.example.zchan_player_annotating;

public class JniImp {

    // Used to load the 'zchan_player_annotating' library on application startup.
    static {
        System.loadLibrary("zchan_player_annotating");
    }
    public static native void startPlay(String path);
    public static native void stopPlay();
    public static native void setSurface(Object surface);

}