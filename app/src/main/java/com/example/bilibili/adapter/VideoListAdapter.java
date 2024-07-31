package com.example.bilibili.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.bilibili.bean.LiveRoom;
import com.example.utils.SizeUtils;
//import com.example.zchan_player.LiveActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Mr.Chan
 * Time 2023-09-06
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class VideoListAdapter extends RecyclerView.Adapter {
    private final ArrayList<Drawable> mDrawables2;
    private ArrayList<LiveRoom> roomList;
    private Context context;
    private ViewPager mBannerView;
    private Handler mHandler = new Handler();

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int type = holder.getItemViewType();
        if (type == 0) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
            p.setFullSpan(true);
        }
    }


    public VideoListAdapter(Context context, ArrayList roomList) {
        this.context = context;
        this.roomList = roomList;
        mDrawables2 = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            mDrawables2.add(context.getResources().getDrawable(com.example.view.R.drawable.search_loading_0));
        }
    }


    public class VideoHolder extends RecyclerView.ViewHolder {
        ImageView itemPic;
        TextView itemName, itemUpName;
        ViewPager itemBannar;

        public VideoHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) {
                itemBannar = itemView.findViewById(com.example.view.R.id.banner);
            } else {
                itemPic = itemView.findViewById(com.example.view.R.id.videoImage);
                itemName = itemView.findViewById(com.example.view.R.id.videoNameText);
                itemUpName = itemView.findViewById(com.example.view.R.id.videoUpText);
            }
        }
    }

    @NonNull
    @Override
    public VideoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0)
            view = LayoutInflater.from(context).inflate(com.example.view.R.layout.main_video_bannar_item, parent, false);
        else
            view = LayoutInflater.from(context).inflate(com.example.view.R.layout.main_video_list_item, parent, false);

        return new VideoHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return 0;
        return position;

    }

    private void start() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(bannerRunnable, 3000);
    }

    private Runnable bannerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mBannerView.getCurrentItem() == 3)
                mBannerView.setCurrentItem(0);
            else mBannerView.setCurrentItem(mBannerView.getCurrentItem() + 1);
            mHandler.postDelayed(bannerRunnable, 1000);
        }
    };

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        VideoHolder videoHolder = (VideoHolder) holder;
        if (getItemViewType(position) == 0) {
            mBannerView = videoHolder.itemBannar;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mBannerView.getLayoutParams();
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
            lp.height = SizeUtils.dip2px(context, 200);
            start();
            mBannerView.setLayoutParams(lp);
            mBannerView.setAdapter(new BannerAdapter(mDrawables2));
            mBannerView.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    start();
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

        } else {
            Picasso.get().load(roomList.get(position - 1).getRoomPic()).into(videoHolder.itemPic);
            videoHolder.itemName.setText(roomList.get(position - 1).getRoomName());
            videoHolder.itemUpName.setText(roomList.get(position - 1).getRoomUpName());
            videoHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(context, LiveActivity.class);
//                    intent.putExtra("url", roomList.get(position - 1).getRoomUrl());
//                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return roomList.size() + 1;
    }
}
