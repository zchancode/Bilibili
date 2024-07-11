package com.example.zchan_player_handwrite.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.zchan_player_handwrite.JniImp;
import com.example.zchan_player_handwrite.R;

public class HPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hplayer);
        JniImp.init();
    }
}