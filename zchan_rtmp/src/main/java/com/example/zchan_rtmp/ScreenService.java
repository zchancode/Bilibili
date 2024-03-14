package com.example.zchan_rtmp;

import static com.example.zchan_rtmp.LiveImp.*;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
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
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;

/**
 * Created by Mr.Chan
 * Time 2024-01-31
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class ScreenService extends Service {

    static {
        System.loadLibrary("zchan_rtmp");
    }

    private static final int ONGOING_NOTIFICATION_ID = 1;
    private static final int VIRTUAL_DISPLAY_FLAGS = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjectionManager mProjectionManager;
    private ImageReader mImageReader;

    boolean isRunning = true;
    boolean isExit = false;
    public double calculateDecibels(byte[] audioData, int readSize) {
        long sum = 0;
        // 将字节转换为 16 位 PCM 值
        for (int i = 0; i < readSize; i += 2) {
            int sample = (audioData[i + 1] << 8) | (audioData[i] & 0xFF);
            sum += Math.abs(sample);
        }
        double average = sum / (readSize / 2.0);

        // 转换为分贝
        double decibel = 20 * Math.log10(average / 32768.0);
        return decibel;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private AudioRecord mRecord;
    private Thread audio_thread = new Thread(new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.Q)
        @SuppressLint("MissingPermission")
        @Override
        public void run() {
            audioRecord();
        }
    });
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("MissingPermission")
    public void audioRecord() {
        AudioRecord.Builder builder = new AudioRecord.Builder();
        builder.setAudioFormat(new AudioFormat.Builder()
                        .setSampleRate(44100)
                        .setChannelMask(AudioFormat.CHANNEL_IN_MONO)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .build())
                .setBufferSizeInBytes(1024 * 2 * 2);
        AudioPlaybackCaptureConfiguration config =
                new AudioPlaybackCaptureConfiguration.Builder(mMediaProjection)
                        .addMatchingUsage(AudioAttributes.USAGE_MEDIA)
                        .addMatchingUsage(AudioAttributes.USAGE_UNKNOWN)
                        .addMatchingUsage(AudioAttributes.USAGE_GAME)
                        .build();
        builder.setAudioPlaybackCaptureConfig(config);
        AudioRecord mRecord = builder.build();
        mRecord.startRecording();

        byte[] buffer = new byte[1024 * 2 * 2];
        while (isRunning) {
            int num = mRecord.read(buffer, 0, 1024 * 2 * 2);
            pushAudio(buffer, num);
            double mVolume = calculateDecibels(buffer, num);
            Log.d("InnerSoundService", "InnerSoundService: " + mVolume);
        }

        isExit = true;

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 将服务转为前台服务
        startForeground(ONGOING_NOTIFICATION_ID, getNotification());

        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        // 获取屏幕录制的结果数据
        int resultCode = intent.getIntExtra("result_code", -1);
        Intent resultData = intent.getParcelableExtra("result_data");


        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, resultData);

        // 创建ImageReader
        mImageReader = ImageReader.newInstance(320, 640, PixelFormat.RGBA_8888, 2);

        // 创建虚拟显示屏
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenRecordService",
                320, 640, 128,
                VIRTUAL_DISPLAY_FLAGS,
                mImageReader.getSurface(), null, null);


        init(320, 640,"rtmp://172.20.10.2:1935/live/hls");
        audio_thread.start();
        mImageReader.acquireNextImage();

        // 设置ImageReader的回调函数
            mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {
                Image image = null;
                try {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        Image.Plane[] planes = image.getPlanes();
                        ByteBuffer buffer = planes[0].getBuffer();

                        int width = image.getWidth();
                        int height = image.getHeight();


                        // 创建一个Bitmap对象
                        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        bitmap.copyPixelsFromBuffer(buffer);

                        // 创建一个Mat对象
                        Mat mat = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
                        Utils.bitmapToMat(bitmap, mat);
                        Log.e("TAG", "onImageAvailable: " + mat.getNativeObjAddr());
//                        pushVideo(mat.getNativeObjAddr());
                        mat.release();

                    }
                } finally {
                    if (image != null) {
                        image.close();
                    }
                }
            }
        }, null);
        startPush();
        return START_STICKY;
    }

    @SuppressLint("NewApi")
    private Notification getNotification() {
         NotificationChannel channel = new NotificationChannel("channel_01",
                "Screen Record Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);
        return new Notification.Builder(this, "channel_01")
                .setContentTitle("屏幕录制")
                .setContentText("正在进行屏幕录制...")
                .build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    }
}

