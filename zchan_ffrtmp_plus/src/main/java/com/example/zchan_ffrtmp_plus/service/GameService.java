package com.example.zchan_ffrtmp_plus.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioPlaybackCaptureConfiguration;
import android.media.AudioRecord;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.example.zchan_ffrtmp_plus.JniImp;

import java.nio.ByteBuffer;

/**
 * Created by Mr.Chan
 * Time 2024-03-21
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class GameService extends Service {

    private MediaProjectionManager mProjectionManager;
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

    private MediaProjection mMediaProjection;
    boolean isRunning = true;
    boolean isExit = false;
    private Thread audioThread = new Thread(new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @Override
        public void run() {
            audioRecord();
        }
    });
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    public void audioRecord() {
        AudioRecord.Builder builder = new AudioRecord.Builder();
        builder.setAudioFormat(new AudioFormat.Builder()
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_IN_STEREO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build())
                .setBufferSizeInBytes(1024 * 2 * 2);
        @SuppressLint("WrongConstant") AudioPlaybackCaptureConfiguration config =
                new AudioPlaybackCaptureConfiguration.Builder(mMediaProjection)
                        .addMatchingUsage(AudioAttributes.ALLOW_CAPTURE_BY_ALL)
                        .build();
        builder.setAudioPlaybackCaptureConfig(config);
        AudioRecord mRecord = builder.build();
        mRecord.startRecording();


        AudioRecord mMicRecord = new AudioRecord(
                MediaRecorder.AudioSource.MIC, 44100,
                AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT,
                1024 * 2 * 2);

        mMicRecord.startRecording();

        byte[] bufferInner = new byte[1024 * 2 * 2];
        byte[] bufferMic = new byte[1024 * 2 * 2];
        byte[] buffer = new byte[1024 * 2 * 2];
        while (isRunning) {
            int numInr = mRecord.read(bufferInner, 0, 1024 * 2 * 2);
            int numMic = mMicRecord.read(bufferMic, 0, 1024 * 2 * 2);
            for (int i = 0; i < numInr; i++) {
                buffer[i] = (byte) (bufferInner[i] * 0.5 + bufferMic[i] * 0.5);
            }
            JniImp.pushPCM(buffer);
        }

        isExit = true;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("NewApi")
    private Notification getNotification() {
        NotificationChannel channel = new NotificationChannel("channel_01",
                "Screen Record Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        return new Notification.Builder(this, "channel_01")
                .setContentTitle("Screen Record Service")
                .setContentText("Recording...")
                .build();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(1, getNotification());
        //getSupportedSizes
        int screenWidth = 320;
        int screenHeight = 640;
        int screenDensity = 128;
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        int resultCode = intent.getIntExtra("result_code", -1);
        Intent resultData = intent.getParcelableExtra("result_data");
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, resultData);
        mImageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenRecordService",
                screenWidth, screenHeight, screenDensity,
                VIRTUAL_DISPLAY_FLAGS,
                mImageReader.getSurface(), null, null);
        JniImp.init("rtmp://139.224.68.119:1935/rtmplive_demo/hls", screenWidth, screenHeight);
        audioThread.start();
        mImageReader.acquireNextImage();
        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = null;
                try {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        //rgba to nv12
                        Log.e("TAG", "onImageAvailable: isAvailable");
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        JniImp.pushRGB(bytes);
                    }
                } catch (Exception e) {

                } finally {
                    if (image != null) {
                        image.close();
                    }
                }
            }

        }, null);
        JniImp.startFRtmp();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        while (!isExit) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
        JniImp.stopFRtmp();


    }
}

