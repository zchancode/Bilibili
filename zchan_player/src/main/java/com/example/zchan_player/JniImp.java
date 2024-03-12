package com.example.zchan_player;

/**
 * Created by Mr.Chan
 * Time 2024-02-26
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class JniImp {
    static {
        System.loadLibrary("zchan_player");
    }

    public static native void setSurface(Object surface);

    public static native void playVideo(String path);

    public static native void stopVideo();

    public static native void pauseVideo();

    public static native void resumeVideo();

    public static native void seekTo(int position);

    public static native int getCurrentPosition();
}
