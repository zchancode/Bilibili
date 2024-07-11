package com.java.string;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bilibili.R;

public class StringActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.string_activity_main);

        StringBuilder stringBuilder = new StringBuilder();//线程不安全
        stringBuilder.append("Hello").append(" ").append("World").reverse();

        StringBuffer stringBuffer = new StringBuffer();//线程安全
        stringBuffer.append("Hello").append(" ").append("World").reverse();

        System.out.println(stringBuilder.toString());

    }
}