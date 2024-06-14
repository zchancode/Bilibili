package com.example.zchan_hardrtmp.service;

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
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.example.zchan_hardrtmp.JniImp;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Mr.Chan
 * Time 2024-03-21
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class GameService extends Service {

    private MediaProjectionManager mProjectionManager;
    private MediaProjection mMediaProjection;
    private MediaCodec videoCodec;
    private MediaCodec audioCodec;
    private VirtualDisplay virtualDisplay;
    private long timeStamp;
    private boolean isRunning = true;
    private boolean isExit = false;

    private Thread decodeAudioThread = new Thread(new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
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


            MediaFormat format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 2);
            format.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 100 * 1024);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel
                    .AACObjectLC);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 64_000);
            try {
                audioCodec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
                audioCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
                audioCodec.start();
            } catch (Exception ignored) {}
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            byte[] bufferInner = new byte[1024 * 2 * 2];
            byte[] bufferMic = new byte[1024 * 2 * 2];
            byte[] buffer = new byte[1024 * 2 * 2];
            while (isRunning) {
                int numInr = mRecord.read(bufferInner, 0, 1024 * 2 * 2);
                mMicRecord.read(bufferMic, 0, 1024 * 2 * 2);
                for (int i = 0; i < numInr; i++) {
                    buffer[i] = (byte) (bufferInner[i] * 0.5 + bufferMic[i] * 0.5);
                }

                int index = audioCodec.dequeueInputBuffer(0);
                ByteBuffer inputBuffer = audioCodec.getInputBuffer(index);
                inputBuffer.clear();
                inputBuffer.put(buffer, 0, numInr);
                audioCodec.queueInputBuffer(index, 0, numInr,
                        System.nanoTime() / 1000, 0);
                index = audioCodec.dequeueOutputBuffer(bufferInfo, 0);
                while (index >= 0 && isRunning) {
                    ByteBuffer outputBuffer = audioCodec.getOutputBuffer(index);
                    byte[] outData = new byte[bufferInfo.size];
                    outputBuffer.get(outData);
                    JniImp.pushAAC(outData, outData.length, System.currentTimeMillis());
                    audioCodec.releaseOutputBuffer(index, false);
                    index = audioCodec.dequeueOutputBuffer(bufferInfo, 0);
                }
            }

            isExit = true;
        }
    });

    private Thread decodeVideoThread = new Thread(new Runnable() {
        @Override
        public void run() {
            videoCodec.start();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            while (isRunning) {
                if (System.currentTimeMillis() - timeStamp >= 2000) {
                    Bundle params = new Bundle();
                    params.putInt(MediaCodec.PARAMETER_KEY_REQUEST_SYNC_FRAME, 0);
                    videoCodec.setParameters(params);
                    timeStamp = System.currentTimeMillis();
                }
                int index = videoCodec.dequeueOutputBuffer(bufferInfo, 100000);
                if (index >= 0) {
                    ByteBuffer buffer = videoCodec.getOutputBuffer(index);
                    byte[] outData = new byte[bufferInfo.size];
                    buffer.get(outData);
                    JniImp.pushH264(outData, outData.length, System.currentTimeMillis());
                    videoCodec.releaseOutputBuffer(index, false);
                }
            }
            isExit = true;
        }
    });

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
        JniImp.init("rtmp://139.224.68.119:1935/rtmplive_demo/hls");
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        int resultCode = intent.getIntExtra("result_code", -1);
        Intent resultData = intent.getParcelableExtra("result_data");
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, resultData);

        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC,
                320,
                640);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 1024000000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, 15);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        //setting level to high
        try {
            videoCodec = MediaCodec.createEncoderByType("video/avc");
            videoCodec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            Surface surface = videoCodec.createInputSurface();
            virtualDisplay = mMediaProjection.createVirtualDisplay(
                    "screen-codec",
                    320, 640, 1,
                    DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                    surface, null, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        decodeVideoThread.start();
        decodeAudioThread.start();
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

        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
        if (virtualDisplay != null) {
            virtualDisplay.release();
            virtualDisplay = null;
        }
        if (videoCodec != null) {
            videoCodec.stop();
            videoCodec.release();
            videoCodec = null;
        }
        if (audioCodec != null) {
            audioCodec.stop();
            audioCodec.release();
            audioCodec = null;
        }



    }
}

