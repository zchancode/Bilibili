package com.example.zchan_orgrtmp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.zchan_orgrtmp.JniImp;
import com.example.zchan_orgrtmp.R;

public class OrgRTMPActivity extends AppCompatActivity {

    static {
        System.loadLibrary("zchan_orgrtmp");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_org_rtmpactivity);
        Toast.makeText(this, JniImp.stringFromJNI(), Toast.LENGTH_SHORT).show();
    }


}