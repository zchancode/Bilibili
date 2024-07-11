package com.java.pattern;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bilibili.R;


public class DesignActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.design_activity_main);
        SingletonType1 singletonType1 = SingletonType1.INSTANCE.getInstance();
        SingletonType2 singletonType2 = SingletonType2.Companion.getInstance();
    }
}