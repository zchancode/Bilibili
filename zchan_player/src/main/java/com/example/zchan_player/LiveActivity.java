package com.example.zchan_player;

import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.zchan_player.adapter.CommentAdapter;
import com.example.zchan_player.view.PlayerSurface;

/**
 * Created by Mr.Chan
 * Time 2024-02-26
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class LiveActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private PlayerSurface mPlayerSurface;



    private String url;
    private Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            JniImp.playVideo(url);
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xplay_activity_live);
        //get intent data
        this.url = getIntent().getStringExtra("url");
        Log.d("zchan", "url: " + url);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mPlayerSurface = (PlayerSurface) findViewById(R.id.playerSurface2);
        mPlayerSurface.setOnSurfaceListener(new PlayerSurface.OnSurfaceListener() {
            @Override
            public void onSurfaceCreated(SurfaceHolder holder) {
                JniImp.setSurface(holder.getSurface());
                mThread.start();
            }
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        String[] myDataset = {"Mike:", "ZChan:", "Aim:", "Jack:", "Tom:", "Jerry"};
        CommentAdapter mAdapter = new CommentAdapter(myDataset);
        recyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onDestroy() {
        JniImp.stopVideo();
        mThread.interrupt();
        super.onDestroy();
    }
}
