package com.example.bilibili.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bilibili.R;

/**
 * Created by Mr.Chan
 * Time 2023-09-05
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class CategoryActivity extends Fragment {
    private View root;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.activity_category, container, false);
        }
        return root;
    }
}