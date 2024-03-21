package com.example.zchan_player_plus.ui;


import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.zchan_player_plus.JniImp;
import com.example.zchan_player_plus.R;
import com.example.zchan_player_plus.view.PlayerSurface;


public class PlayerActivity extends AppCompatActivity {
    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            JniImp.startPlay("/sdcard/input.mp4");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_plus);
        ((PlayerSurface)findViewById(R.id.playerSurface)).setOnSurfaceListener(holder -> {
            JniImp.setSurface(holder.getSurface());
            mThread.start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JniImp.stopPlay();
        mThread.interrupt();
    }
}