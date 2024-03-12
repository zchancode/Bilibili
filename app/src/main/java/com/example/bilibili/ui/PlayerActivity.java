package com.example.bilibili.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.example.bilibili.R;
import com.example.zchan_player.PlayerView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class PlayerActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewpager;
    ArrayList fragmentList = new ArrayList<Fragment>();
    String[] temp = {"简介", "评论"};
    private PlayerView mXPlayerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        tabLayout = findViewById(R.id.playerTab);
        viewpager = findViewById(R.id.playerContent);
        MPagerAdapter mPagerAdapter = new MPagerAdapter(getSupportFragmentManager());
        initFragment();
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setAdapter(mPagerAdapter);
        mXPlayerView = findViewById(R.id.x_play_view);
        mXPlayerView.setUrl("/sdcard/v1080.mp4");
        mXPlayerView.play();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mXPlayerView.isPlay) {
            mXPlayerView.closePlayer();
        }
    }

    private void initFragment() {
        fragmentList.add(new IntroduceActivity());
        fragmentList.add(new DynamicActivity());
    }


    class MPagerAdapter extends FragmentPagerAdapter {

        public MPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return (Fragment) fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return temp[position];
        }
    }


}