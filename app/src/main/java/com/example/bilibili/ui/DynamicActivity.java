package com.example.bilibili.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bilibili.R;
import com.example.bilibili.adapter.DynamicAdapter;
import com.example.bilibili.bean.RecentLookUser;

import java.util.ArrayList;
import java.util.List;

public class DynamicActivity extends Fragment {
    private View root;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.activity_dynamic, container, false);
        }
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setHorizontalScrollBarEnabled(false);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(horizontalLayoutManager);

        List<RecentLookUser> myDataset = new ArrayList<>();
        myDataset.add(new RecentLookUser("张三","http://www.baidu.com"));


        DynamicAdapter myAdapter = new DynamicAdapter(myDataset);
        recyclerView.setAdapter(myAdapter);

        return root;
    }
}