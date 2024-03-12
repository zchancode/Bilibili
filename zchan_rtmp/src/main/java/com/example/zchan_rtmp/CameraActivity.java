package com.example.zchan_rtmp;

import static com.example.zchan_rtmp.LiveImp.*;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    static {
        System.loadLibrary("zchan_rtmp");
    }

    private JavaCameraView cameraView;
    boolean isRunning = true;
    boolean isExit = false;

    private Thread audio_thread = new Thread(new Runnable() {
        @Override
        public void run() {
            audioRecord();
        }
    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        //remove toolbar and status bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        cameraView = findViewById(R.id.camera_view_test);
        cameraView.setCvCameraViewListener(this);
        cameraView.setCameraPermissionGranted();
        cameraView.setCameraIndex(1);
        cameraView.enableView();
    }

    private AudioRecord mRecord;

    @SuppressLint("MissingPermission")
    public void audioRecord() {
        mRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC,
                44100,
                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, 1024 * 8);
        mRecord.startRecording();

        byte[] buffer = new byte[1024 * 8];
        while (isRunning) {
            int num = mRecord.read(buffer, 0, 1024 * 8);
            pushAudio(buffer, num);
        }

        isExit = true;

    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        isRunning = true;
        isExit = false;
        audio_thread.start();
        //be rotated 90 degrees
        init(height, width,"");
        startPush();
    }

    @Override
    public void onCameraViewStopped() {
        mRecord.stop();
        isRunning = false;
        while (!isExit) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.e("TAG", "pushAudio stop");
        stopPush();
    }

    Mat mat = new Mat();

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame cvCameraViewFrame) {
        Mat mat = cvCameraViewFrame.rgba();
        Core.flip(mat, mat, 1);
        pushVideo(mat.getNativeObjAddr());
        return mat;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraView != null) {
            cameraView.disableView();
            mRecord.stop();
            mat.release();
            audio_thread.interrupt();
            stopPush();
        }
    }
}
