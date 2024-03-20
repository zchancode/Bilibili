package com.example.zchan_faacencoder.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import com.example.zchan_faacencoder.JniImp;
import com.example.zchan_faacencoder.R;

public class FaacEncoderActivity extends AppCompatActivity {
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
            mRecord.read(buffer, 0, 1024 * 2 * 2);
            JniImp.pushPCM(buffer);
        }

        isExit = true;

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faac_encoder);
        JniImp.initEncoder(44100, 2);
        audio_thread.start();
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
        JniImp.stopEncoder();
        super.onDestroy();
    }
}