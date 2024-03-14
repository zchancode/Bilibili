package com.example.zchan_rtmp;

import static com.example.zchan_rtmp.LiveImp.init;
import static com.example.zchan_rtmp.LiveImp.pushAudio;
import static com.example.zchan_rtmp.LiveImp.pushVideo;
import static com.example.zchan_rtmp.LiveImp.pushVideoRGBA;
import static com.example.zchan_rtmp.LiveImp.startPush;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Provider;

/**
 * Created by Mr.Chan
 * Time 2024-03-13
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class InnerSoundService extends Service {
    static {
        System.loadLibrary("zchan_rtmp");
    }
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

    public void writeBytes(byte[] array) {
        FileOutputStream writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            writer = new FileOutputStream("/sdcard/Download/bbb.pcm", true);
            writer.write(array);
            writer.write('\n');


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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
        AudioPlaybackCaptureConfiguration config =
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
        while (isRunning) {
            int numInr = mRecord.read(bufferInner, 0, 1024 * 2 * 2);
            int numMic = mMicRecord.read(bufferMic, 0, 1024 * 2 * 2);
            double mVolumeInr = calculateDecibels(bufferInner, numInr);
            double mVolumeMic = calculateDecibels(bufferMic, numMic);
            pushAudio(bufferInner, numInr);
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
        mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        int resultCode = intent.getIntExtra("result_code", -1);
        Intent resultData = intent.getParcelableExtra("result_data");
        mMediaProjection = mProjectionManager.getMediaProjection(resultCode, resultData);
        mImageReader = ImageReader.newInstance(320, 640, PixelFormat.RGBA_8888, 2);
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("ScreenRecordService",
                320, 640, 128,
                VIRTUAL_DISPLAY_FLAGS,
                mImageReader.getSurface(), null, null);
        init(320, 640,"rtmp://172.20.10.2:1935/live/hls");
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
                        int width = image.getWidth();
                        int height = image.getHeight();
                        buffer.get(bytes);
                        pushVideoRGBA(bytes, width, height);
                    }
                } catch (Exception e) {

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
