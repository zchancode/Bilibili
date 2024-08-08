package com.example.playercore;

/**
 * Created by Mr.Chan
 * Time 2024-08-07
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class Log {
    public static LogListener logListener;

    public interface LogListener {
        void onMessage(String message);
    }

    public static void setLogListener(LogListener listener) {
        logListener = listener;
    }


    public static void onMessage(String message) {
        if (logListener != null) {
            logListener.onMessage(message);
        }
    }
}
