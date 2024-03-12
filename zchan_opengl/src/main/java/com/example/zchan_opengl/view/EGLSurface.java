package com.example.zchan_opengl.view;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Mr.Chan
 * Time 2024-03-04
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class EGLSurface extends GLSurfaceView implements SurfaceHolder.Callback, GLSurfaceView.Renderer {

    public EGLSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRenderer(this);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (onSurfaceListener != null) {
            onSurfaceListener.onSurfaceCreated(holder);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {

    }

    @Override
    public void onDrawFrame(GL10 gl10) {

    }

    private OnSurfaceListener onSurfaceListener;
    public void setOnSurfaceListener(OnSurfaceListener onSurfaceListener) {
        this.onSurfaceListener = onSurfaceListener;
    }
    public interface OnSurfaceListener {
        void onSurfaceCreated(SurfaceHolder holder);
    }

}
