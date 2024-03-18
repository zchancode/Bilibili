package com.example.zchan_structure.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.zchan_structure.JniImp;
import com.example.zchan_structure.R;
import com.example.zchan_structure.databinding.ActivityStructureBinding;

public class StructureActivity extends AppCompatActivity {
    private ActivityStructureBinding binding;
    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            JniImp.startPlay("/sdcard/input.mp4");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStructureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.playerSurface.setOnSurfaceListener(holder -> {
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