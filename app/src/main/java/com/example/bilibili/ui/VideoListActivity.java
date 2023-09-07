package com.example.bilibili.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.bilibili.R;
import com.example.bilibili.adapter.BannerAdapter;
import com.example.bilibili.adapter.VideoListAdapter;
import com.example.utils.SizeUtils;

import java.util.ArrayList;

/**
 * Created by Mr.Chan
 * Time 2023-09-05
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class VideoListActivity extends Fragment {
    private View root;
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root == null) {
            root = inflater.inflate(R.layout.activity_video_list, container, false);
        }
        mRecyclerView = root.findViewById(R.id.videoList);
        ArrayList<Drawable> drawables = new ArrayList<>();
        ArrayList<String> videoNames=new ArrayList<>();
        ArrayList<String> videoUpNames=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            videoNames.add("中低端芯片大横");
            videoUpNames.add("极客湾");
            drawables.add(getResources().getDrawable(com.example.view.R.drawable.search_loading_1));
        }

        VideoListAdapter adapter = new VideoListAdapter(getContext(),drawables,videoNames,videoUpNames);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                layoutManager.invalidateSpanAssignments();
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);



        return root;
    }
}
