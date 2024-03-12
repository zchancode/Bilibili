package com.example.zchan_rtmp;


import static com.example.zchan_rtmp.LiveImp.pushAudio;

import android.annotation.SuppressLint;
import android.graphics.ImageFormat;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.core.UseCase;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;

/**
 * Created by Mr.Chan
 * Time 2024-02-28
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class CameraXActivity extends AppCompatActivity {
    private Preview preview = null;
    private ImageAnalysis imageAnalysis;
    private PreviewView viewFinder;
    private String url;
    private boolean isRunning = true;
    private boolean isExit = false;
    private Thread audio_thread = new Thread(new Runnable() {
        @Override
        public void run() {
            audioRecord();
        }
    });
    private AudioRecord mRecord;

    @SuppressLint("MissingPermission")
    public void audioRecord() {
        mRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,//sample rate
                AudioFormat.CHANNEL_IN_STEREO,//2 channel
                AudioFormat.ENCODING_PCM_16BIT,//16bit
                1024 * 2 * 2);//nb_samples * 16bit * nb_channels
        mRecord.startRecording();


        byte[] buffer = new byte[1024 * 2 * 2];
        while (isRunning) {
            int num = mRecord.read(buffer, 0, 1024 * 2 * 2);
            pushAudio(buffer, num);
        }

        isExit = true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);
        this.url = getIntent().getStringExtra("url");
        viewFinder = findViewById(R.id.viewFinder);
        startCamera();
    }


    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        //get size of camera

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // Handle any errors
            }
        }, ContextCompat.getMainExecutor(this));
    }

    boolean isInited = false;


    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        Preview preview = new Preview.Builder()
                .build();

        imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), image -> {
            int format = image.getFormat();
            int width, height;
            width = image.getWidth();
            height = image.getHeight();
            if (!isInited) {
                isRunning = true;
                isExit = false;
                audio_thread.start();
                LiveImp.init(width, height, url);
                LiveImp.startPush();
                isInited = true;
            }
            //loge format is which type of image not number is type example 17 is YUV_420_888
            if (format == ImageFormat.YUV_420_888 || format == ImageFormat.YUV_422_888 || format == ImageFormat.YUV_444_888) {
                ByteBuffer yBuffer = image.getPlanes()[0].getBuffer();
                ByteBuffer uvBuffer = image.getPlanes()[1].getBuffer();
                //give up u data
                byte[] yBytes = new byte[yBuffer.remaining()];//yy yy yy yy
                byte[] uvBytes = new byte[uvBuffer.remaining()];//uv uv
                yBuffer.get(yBytes);
                uvBuffer.get(uvBytes);

                LiveImp.pushVideoYUV420NV12(yBytes, uvBytes, width, height);

                image.close();
            }
        });

        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());

        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);
    }


    @Override
    protected void onDestroy() {
        isRunning = false;
        while (!isExit) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mRecord.stop();
        audio_thread.interrupt();
        LiveImp.stopPush();

        super.onDestroy();
    }
}