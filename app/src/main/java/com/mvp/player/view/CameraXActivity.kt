package com.mvp.player.view

import android.os.Bundle
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.bilibili.databinding.ActivityCameraXactivityBinding
import com.google.common.util.concurrent.ListenableFuture

import com.mvp.player.view.base.XBaseActivity

class CameraXActivity : XBaseActivity() {

    private lateinit var binding: ActivityCameraXactivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(object : Runnable {
            override fun run() {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@CameraXActivity,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview
                )
            }
        }, ContextCompat.getMainExecutor(this))

    }
}