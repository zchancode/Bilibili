package com.example.zchan_rtmp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class CreateRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        findViewById(R.id.button).setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraXActivity.class);
            intent.putExtra("url", "rtmp://172.20.10.2:1935/room1/hls");
            startActivity(intent);
        });
    }
}