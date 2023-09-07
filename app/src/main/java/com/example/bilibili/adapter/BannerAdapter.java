package com.example.bilibili.adapter;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

/**
 * Created by Mr.Chan
 * Time 2023-09-07
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class BannerAdapter extends PagerAdapter {

    private List<Drawable> bannerImageList;

    public BannerAdapter(List<Drawable> bannerImageList) {
        this.bannerImageList = bannerImageList;
    }

    @Override
    public int getCount() {
        return bannerImageList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView bannerImageView = new ImageView(container.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        bannerImageView.setLayoutParams(lp);
        bannerImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        bannerImageView.setImageDrawable(bannerImageList.get(position));
        container.addView(bannerImageView);
        return bannerImageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}