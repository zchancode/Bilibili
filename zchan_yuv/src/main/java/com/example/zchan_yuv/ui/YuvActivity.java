package com.example.zchan_yuv.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zchan_yuv.R;

/**
 * Created by Mr.Chan
 * Time 2024-03-03
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */

public class YuvActivity extends AppCompatActivity {
    static {
        System.loadLibrary("zchan_yuv");
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yuv);
        playYuv("/sdcard/v1080.mp4");
        ImageView imageView = findViewById(R.id.imageView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(1920, 1080, Bitmap.Config.ARGB_8888);
                while (true) {
                    byte[] rgbFrame = getRgbFrame();
                    if (rgbFrame == null) {
                        continue;
                    }
                    //AV_PIX_FMT_RGB24 to Bitmap
                    bitmap.copyPixelsFromBuffer(java.nio.ByteBuffer.wrap(rgbFrame));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        }).start();


    }

    private native void playYuv(String path);

    private native byte[] getRgbFrame();
}
