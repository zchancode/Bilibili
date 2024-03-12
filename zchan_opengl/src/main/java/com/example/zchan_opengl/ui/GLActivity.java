package com.example.zchan_opengl.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.EGLImage;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.example.zchan_opengl.GLImp;
import com.example.zchan_opengl.R;
import com.example.zchan_opengl.view.EGLSurface;

public class GLActivity extends AppCompatActivity {

    static {
        System.loadLibrary("zchan_opengl");
    }
    private Thread mThread =  new Thread(() -> {
        GLImp.playSurface("http://stream.qhbtv.com/qhws/playlist.m3u8");
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glactivity);
        EGLSurface eglSurface = findViewById(R.id.eglSurface);
        eglSurface.setOnSurfaceListener(holder -> {
            GLImp.setSurface(holder.getSurface());
            mThread.start();
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mThread.interrupt();
        GLImp.closeSurface();
    }
}