package com.example.zchan_structure.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.zchan_structure.JniImp;
import com.example.zchan_structure.R;

public class StructureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_structure);
        Toast.makeText(this, JniImp.stringFromJNI(), Toast.LENGTH_SHORT).show();
    }
}