package com.example.zchan_player_annotating.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.SurfaceHolder;

import com.example.zchan_player_annotating.JniImp;
import com.example.zchan_player_annotating.R;
import com.example.zchan_player_annotating.view.PlayerSurface;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        PlayerSurface playerSurface = findViewById(R.id.playerSurface);
        playerSurface.setOnSurfaceListener(new PlayerSurface.OnSurfaceListener() {
            @Override
            public void onSurfaceCreated(SurfaceHolder holder) {
                JniImp.setSurface(holder.getSurface());
                JniImp.startPlay("/sdcard/input.mp4");
            }
        });
    }

    @Override
    protected void onDestroy() {
        JniImp.stopPlay();
        super.onDestroy();
    }
}