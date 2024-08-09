package com.mvp.player.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import com.example.bilibili.databinding.ActivityCameraXactivityBinding
import com.google.common.util.concurrent.ListenableFuture

import com.mvp.player.view.base.XBaseActivity
import java.io.File

class CameraXActivity : XBaseActivity() {
    private fun allPermissionsGranted() = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    ).all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private lateinit var binding: ActivityCameraXactivityBinding
    private lateinit var videoCapture: VideoCapture<Recorder>
    private var recording: Recording? = null
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var flashOFF = true
    private var cameraCtr: androidx.camera.core.CameraControl? = null


    @SuppressLint("MissingPermission")
    private fun initCameraRecord(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(object : Runnable {
            override fun run() {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build()
                preview.setSurfaceProvider(binding.previewView.surfaceProvider)

                //record
                val recorder = Recorder.Builder()
                    .setQualitySelector(QualitySelector.from(Quality.HD))
                    .build()
                videoCapture = VideoCapture.withOutput(recorder)


                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    this@CameraXActivity,
                    cameraSelector,
                    preview,
                    videoCapture
                )
                cameraCtr = camera.cameraControl
            }
        }, ContextCompat.getMainExecutor(this))

        binding.flipBtn.setOnClickListener {
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
                initCameraRecord()
            } else {
                cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                initCameraRecord()
            }
        }

        binding.flashBtn.setOnClickListener {
            if (flashOFF) {
                cameraCtr?.enableTorch(true)
                flashOFF = false
            } else {
                cameraCtr?.enableTorch(false)
                flashOFF = true
            }
        }

        binding.recordBtn.setOnClickListener {

            if (recording != null) {
                recording?.stop()
                recording = null
                Toast.makeText(this, "stop", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            val videoFile = File("/data/data/com.example.bilibili/rec.mp4")
            val outputOptions = FileOutputOptions.Builder(videoFile).build()
            recording = videoCapture.output.prepareRecording(this, outputOptions)
                .withAudioEnabled()
                .start(ContextCompat.getMainExecutor(this)) {
                    when (it) {
                        is VideoRecordEvent.Start -> {
                            Toast.makeText(this, "start", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraXactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO), 1)
            Toast.makeText(this, "申请权限", Toast.LENGTH_SHORT).show()
        } else {
            initCameraRecord()
            Toast.makeText(this, "已经有权限", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (allPermissionsGranted()){
                initCameraRecord()
                Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show()
            } else {
                finish()
                Toast.makeText(this, "权限申请失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}