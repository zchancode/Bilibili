package com.example.zchan_player;

import static com.example.zchan_player.JniImp.*;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.zchan_player.utils.SizeUtils;
import com.example.zchan_player.view.PlayerSurface;


/**
 * Created by Mr.Chan
 * Time 2023-12-28
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class PlayerView extends ViewGroup {


    static {
        System.loadLibrary("zchan_player");
    }


    private TextView mBackBtn;

    private SeekBar mProcessBar;
    public boolean isPlay = false;
    public boolean isTouch = false;
    private PlayerSurface mPlayerSurface;
    private TextView mPlayBtn;
    private TextView mTvTime;
    private LinearLayout mLayout;

    private String url;
    private Thread mThread;

    private String msToTime(int ms) {
        int s = ms / 1000;
        int m = s / 60;
        int h = m / 60;
        s %= 60;
        m %= 60;
        if (h > 0) {
            return String.format("%02d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }

    public PlayerView(Context context) {
        this(context, null);
    }

    public PlayerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.xplay_activity_main, this, false);
        addView(mLayout);
        mPlayerSurface = findViewById(R.id.playerSurface);
        mProcessBar = findViewById(R.id.processBar);
        mTvTime = findViewById(R.id.tv_time);
        mPlayBtn = findViewById(R.id.play_btn);
        mBackBtn = findViewById(R.id.back_btn);
        mBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                closePlayer();
                ((Activity)getContext()).finish();
            }
        });
        mPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlay) {
                    mPlayBtn.setBackground(getResources().getDrawable(R.drawable.xplayer_play_can_play));
                    resumeVideo();
                } else {
                    mPlayBtn.setBackground(getResources().getDrawable(R.drawable.xplayer_play_can_pause));
                    pauseVideo();
                }
                isPlay = !isPlay;
            }
        });
        mProcessBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int pos, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekTo(seekBar.getProgress());
                seekBar.setProgress(seekBar.getProgress());
            }
        });


    }

    public void closePlayer(){
        isPlay = false;
        mThread.interrupt();
        stopVideo();
    }
    public void setUrl(String url){
        this.url = url;
    }
    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JniImp.playVideo(url);
                isPlay = true;
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mPlayBtn.setBackground(getResources().getDrawable(R.drawable.xplayer_play_can_pause));
                    }
                });
            }
        }).start();
        mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int pos = getCurrentPosition();
                    if (pos > 0) {
                        ((Activity)getContext()).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.e("TAG", "run: "+pos );
                                if (!isTouch)
                                    mProcessBar.setProgress(pos);
                            }
                        });
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mThread.start();
    }

    private int mViewGroupWidth;
    private int mViewGroupHeight;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        mViewGroupWidth = mWidthSize;
        mViewGroupHeight = SizeUtils.dip2px(getContext(), 220);
        setMeasuredDimension(mViewGroupWidth, mViewGroupHeight);
        mLayout.measure(MeasureSpec.makeMeasureSpec(mViewGroupWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mViewGroupHeight, MeasureSpec.EXACTLY));

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        View view = getChildAt(0);
        view.layout(0, 0, mViewGroupWidth, mViewGroupHeight);

    }



}
