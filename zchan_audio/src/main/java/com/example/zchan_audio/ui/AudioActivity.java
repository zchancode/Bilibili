package com.example.zchan_audio.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.zchan_audio.R;

public class AudioActivity extends AppCompatActivity {
    static {
        System.loadLibrary("zchan_audio");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        playAudio("rtmp://172.20.10.2:1935/live/hls");
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);

        button.setOnClickListener(v -> {
            stopAudio();
            Toast.makeText(this, "Stop audio", Toast.LENGTH_SHORT).show();
        });

        button2.setOnClickListener(v -> {
            pauseAudio();
            Toast.makeText(this, "Pause audio", Toast.LENGTH_SHORT).show();
        });


    }
    private native void playAudio(String path);
    private native void stopAudio();
    private native void pauseAudio();
}