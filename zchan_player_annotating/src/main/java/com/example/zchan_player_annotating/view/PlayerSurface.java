package com.example.zchan_player_annotating.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Mr.Chan
 * Time 2023-12-28
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class PlayerSurface extends GLSurfaceView implements SurfaceHolder.Callback, GLSurfaceView.Renderer {

    private OnSurfaceListener onSurfaceListener;
    public void setOnSurfaceListener(OnSurfaceListener onSurfaceListener) {
        this.onSurfaceListener = onSurfaceListener;
    }
    public interface OnSurfaceListener {
        void onSurfaceCreated(SurfaceHolder holder);
    }
    public PlayerSurface(Context context, AttributeSet attrs) {
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
    public void surfaceChanged(SurfaceHolder var1, int var2, int var3, int var4) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder var1) {

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
}
