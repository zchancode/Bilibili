package com.example.bilibili.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.example.bilibili.R;
import com.example.utils.SizeUtils;

import java.util.ArrayList;

/**
 * Created by Mr.Chan
 * Time 2023-09-06
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class VideoListAdapter extends RecyclerView.Adapter {
    private final ArrayList<Drawable> mDrawables2;
    public ArrayList<String> itemNameList, itemUpNameList;
    private Context context;
    public ArrayList<Drawable> itemPicList;
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


    public VideoListAdapter(Context context, ArrayList itemPicList, ArrayList itemNameList, ArrayList itemUpNameList) {
        this.context = context;
        this.itemPicList = itemPicList;
        this.itemNameList = itemNameList;
        this.itemUpNameList = itemUpNameList;
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        VideoHolder videoHolder = (VideoHolder) holder;
        if (getItemViewType(position) == 0) {
            ViewPager bannerView = videoHolder.itemBannar;
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) bannerView.getLayoutParams();
            lp.width = LinearLayout.LayoutParams.MATCH_PARENT;
            lp.height = SizeUtils.dip2px(context, 200);
            bannerView.setLayoutParams(lp);
            bannerView.setAdapter(new BannerAdapter(mDrawables2));
        } else {
            videoHolder.itemPic.setImageDrawable(itemPicList.get(position-1));
            videoHolder.itemName.setText(itemNameList.get(position-1));
            videoHolder.itemUpName.setText(itemUpNameList.get(position-1));
        }
    }

    @Override
    public int getItemCount() {
        return itemNameList.size()+1;
    }
}
