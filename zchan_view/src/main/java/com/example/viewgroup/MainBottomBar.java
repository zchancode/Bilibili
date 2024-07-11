package com.example.viewgroup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorLong;
import androidx.annotation.Nullable;

import com.example.utils.SizeUtils;
import com.example.view.R;

/**
 * Created by Mr.Chan
 * Time 2023-09-05
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class MainBottomBar extends ViewGroup {
    private int mViewGroupWidth;
    private int mViewGroupHeight;

    private TextView postButton;

    private TextView dynamic;
    private TextView home;
    private TextView category;
    private TextView communicate;
    private OnItemOnClickListener mOnItemOnClickListener;

    public void setItem(int item){
        home.setTextColor(Color.parseColor("#999999"));
        home.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_home_unselected),null,null);
        dynamic.setTextColor(Color.parseColor("#999999"));
        dynamic.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_dynamic_unselected),null,null);
        category.setTextColor(Color.parseColor("#999999"));
        category.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_category_unselected),null,null);
        communicate.setTextColor(Color.parseColor("#999999"));
        communicate.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_communicate_unselected),null,null);

        switch (item){

            case 0:
                home.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_home_selected),null,null);
                home.setTextColor(Color.parseColor("#FB7299"));
                break;
            case 1:
                dynamic.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_dynamic_selected),null,null);
                dynamic.setTextColor(Color.parseColor("#FB7299"));
                break;
            case 2:
                break;
            case 3:
                category.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_category_selected),null,null);
                category.setTextColor(Color.parseColor("#FB7299"));
                break;
            case 4:
                communicate.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_communicate_selected),null,null);
                communicate.setTextColor(Color.parseColor("#FB7299"));
                break;
        }

        invalidate();
    }
    public MainBottomBar(Context context) {
        this(context, null);
    }

    public MainBottomBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MainBottomBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        home = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.main_bottom_bar_text,this,false);
        dynamic = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.main_bottom_bar_text,this,false);
        postButton = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.main_bottom_bar_text,this,false);
        category = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.main_bottom_bar_text,this,false);
        communicate = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.main_bottom_bar_text,this,false);
        home.setText("首页");
        home.setTag(0);
        home.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_home_unselected),null,null);
        dynamic.setText("动态");
        dynamic.setTag(1);

        dynamic.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_dynamic_unselected),null,null);
        postButton.setTag(2);

        postButton.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.bili_story_publish_pink),null,null);
        category.setText("目录");
        category.setTag(3);

        category.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_category_unselected),null,null);
        communicate.setText("我的");
        communicate.setTag(4);

        communicate.setCompoundDrawablesWithIntrinsicBounds(null,getResources().getDrawable(R.drawable.ic_communicate_unselected),null,null);


        addView(home);
        addView(dynamic);
        addView(postButton);
        addView(category);
        addView(communicate);
        setItem(0);
        home.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemOnClickListener!=null){
                    setItem(0);
                    mOnItemOnClickListener.onClick((Integer) home.getTag());
                }
            }
        });
        dynamic.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemOnClickListener!=null){
                    setItem(1);
                    mOnItemOnClickListener.onClick((Integer) dynamic.getTag());
                }
            }
        });
        postButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemOnClickListener!=null){
                    mOnItemOnClickListener.onClick((Integer) postButton.getTag());
                }
            }
        });
        category.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemOnClickListener!=null){
                    setItem(3);
                    mOnItemOnClickListener.onClick((Integer) category.getTag());
                }
            }
        });
        communicate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOnItemOnClickListener!=null){
                    setItem(4);
                    mOnItemOnClickListener.onClick((Integer) communicate.getTag());
                }
            }
        });


    }
    public void setItemOnClickListener(OnItemOnClickListener listener){
        this.mOnItemOnClickListener=listener;
    }
    public interface OnItemOnClickListener{
        void onClick(int item);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        mViewGroupWidth=mWidthSize;
        mViewGroupHeight = SizeUtils.dip2px(getContext(), 60);
        setMeasuredDimension(mViewGroupWidth,mViewGroupHeight);
        home.measure(MeasureSpec.makeMeasureSpec(mViewGroupWidth/5,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mViewGroupHeight,MeasureSpec.EXACTLY));
        dynamic.measure(MeasureSpec.makeMeasureSpec(mViewGroupWidth/5,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mViewGroupHeight,MeasureSpec.EXACTLY));
        postButton.measure(MeasureSpec.makeMeasureSpec(mViewGroupWidth/5,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mViewGroupHeight,MeasureSpec.EXACTLY));
        category.measure(MeasureSpec.makeMeasureSpec(mViewGroupWidth/5,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mViewGroupHeight,MeasureSpec.EXACTLY));
        communicate.measure(MeasureSpec.makeMeasureSpec(mViewGroupWidth/5,MeasureSpec.EXACTLY),MeasureSpec.makeMeasureSpec(mViewGroupHeight,MeasureSpec.EXACTLY));

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        home.layout(0,mViewGroupHeight/10,communicate.getMeasuredWidth(),communicate.getMeasuredHeight());
        dynamic.layout(communicate.getMeasuredWidth(),mViewGroupHeight/10,communicate.getMeasuredWidth()*2,communicate.getMeasuredHeight());
        postButton.layout(communicate.getMeasuredWidth()*2,mViewGroupHeight/5,communicate.getMeasuredWidth()*3,communicate.getMeasuredHeight());
        category.layout(communicate.getMeasuredWidth()*3,mViewGroupHeight/10,communicate.getMeasuredWidth()*4,communicate.getMeasuredHeight());
        communicate.layout(communicate.getMeasuredWidth()*4,mViewGroupHeight/10,communicate.getMeasuredWidth()*5,communicate.getMeasuredHeight());
    }

}
