package com.example.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.utils.SizeUtils;

/**
 * Created by Mr.Chan
 * Time 2023-12-27
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class ProcessBar extends View {
    private final Paint paint;
    private double now_progress = 0;
    private double cache_process = 0;

    public ProcessBar(Context context) {
        this(context, null);
    }

    public ProcessBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProcessBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the background of the progress bar
        paint.setColor(Color.parseColor("#44FFFFFF"));
        canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

        paint.setColor(Color.parseColor("#66FFFFFF"));
        canvas.drawRect( 0, 0, (float)(getWidth() * cache_process), getHeight(), paint);

        // Draw the foreground progress of the progress bar
        paint.setColor(Color.parseColor("#FFFD8AAB"));
        float progressWidth = (float) (getWidth() * now_progress);
        canvas.drawRect(0, 0, progressWidth, getHeight(), paint);
    }

    public void setProgress(double now_process, double cache_progress) {
        this.now_progress = now_process;
        this.cache_process = cache_progress;
        Log.e("TAG", "setProgress: " + now_progress + " " + cache_progress );
        invalidate();
    }

}
