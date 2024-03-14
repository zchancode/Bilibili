package com.example.zchan_rtmp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

public class ScreenActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCREEN_RECORD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen);

        // 请求屏幕录制权限
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_CODE_SCREEN_RECORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCREEN_RECORD && resultCode == RESULT_OK) {
            Intent serviceIntent = new Intent(this, InnerSoundService.class);
            serviceIntent.putExtra("result_code", resultCode);
            serviceIntent.putExtra("result_data", data);
            ContextCompat.startForegroundService(this, serviceIntent);
        }
    }
}
