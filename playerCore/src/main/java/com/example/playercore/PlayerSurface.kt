package com.example.playercore

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.SurfaceHolder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
Created by Mr.Chan
Time 2024-07-29
Blog https://www.cnblogs.com/Frank-dev-blog/
 */
class PlayerSurface: GLSurfaceView, GLSurfaceView.Renderer, SurfaceHolder.Callback {
    private var onSurfaceCreated: (holder: SurfaceHolder) -> Unit = {}
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs){
        setRenderer(this)
    }

    public fun setOnSurfaceCreated(onSurfaceCreated: (holder: SurfaceHolder) -> Unit) {
        this.onSurfaceCreated = onSurfaceCreated
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        onSurfaceCreated(holder)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
    }

    override fun onDrawFrame(gl: GL10?) {
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }
}