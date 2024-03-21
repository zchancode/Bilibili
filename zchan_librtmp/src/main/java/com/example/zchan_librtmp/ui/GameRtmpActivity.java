package com.example.zchan_librtmp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.view.View;

import com.example.zchan_librtmp.R;
import com.example.zchan_librtmp.service.GameService;

public class GameRtmpActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCREEN_RECORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_rtmp);

        // 请求屏幕录制权限
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_CODE_SCREEN_RECORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCREEN_RECORD && resultCode == RESULT_OK) {
            Intent serviceIntent = new Intent(this, GameService.class);
            serviceIntent.putExtra("result_code", resultCode);
            serviceIntent.putExtra("result_data", data);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }

    public void stopRecord(View view) {
        Intent serviceIntent = new Intent(this, GameService.class);
        stopService(serviceIntent);
    }
}
