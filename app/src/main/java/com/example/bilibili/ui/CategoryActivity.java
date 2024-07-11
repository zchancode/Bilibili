package com.example.bilibili.ui;

import static android.content.Context.BIND_AUTO_CREATE;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bilibili.R;
import com.example.bilibili.service.TestService;

/**
 * Created by Mr.Chan
 * Time 2023-09-05
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class CategoryActivity extends Fragment {
    private View root;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TestService.TestBinder binder = (TestService.TestBinder) service;
            String process = binder.getProcess();
            System.out.println("process: " + process);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private WebView mWebView;
    private WebSettings mSettings;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.activity_category, container, false);
        }
        Intent intent = new Intent(getActivity(), TestService.class);
        getActivity().bindService(intent, connection, BIND_AUTO_CREATE);

        mWebView = root.findViewById(R.id.webview);
        mSettings = mWebView.getSettings();
        mSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl("file:///android_asset/index.html");


        return root;
    }

}