package com.example.bilibili.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Provider;

/**
 * Created by Mr.Chan
 * Time 2024-04-11
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class TestService extends Service {

    private String process;
    public class TestBinder extends Binder {
        public String getProcess() {
            return process;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL("https://api.oioweb.cn/api/site/icp?domain=qq.com");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();
                    int responseCode = urlConnection.getResponseCode();
                    if (responseCode == 200) {
                        InputStream inputStream =  urlConnection.getInputStream();
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        StringBuilder stringBuilder = new StringBuilder();
                        while ((len = inputStream.read(bytes)) != -1) {
                            stringBuilder.append(new String(bytes, 0, len));
                        }
                        process = stringBuilder.toString();

                    } else {
                        process = "failed";
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
        Log.d("TestService", "onCreate executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TestService", "onStartCommand executed");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("TestService", "onBind executed");
        return new TestBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("TestService", "onUnbind executed");
        return super.onUnbind(intent);

    }

    @Override
    public void onDestroy() {
        Log.d("TestService", "onDestroy executed");
        super.onDestroy();
    }
}
