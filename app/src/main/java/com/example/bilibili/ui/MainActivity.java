package com.example.bilibili.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;

import com.example.bilibili.R;
import com.example.bilibili.databinding.ActivityMainBinding;
import com.example.viewgroup.MainBottomBar;
import com.example.zchan_ffrtmp_plus.ui.FRTMPActivity;
import com.example.zchan_librtmp.ui.GameRtmpActivity;
import com.example.zchan_librtmp.ui.LibRTMPActivity;
import com.example.zchan_rtmp.CameraXActivity;
import com.example.zchan_rtmp.ScreenActivity;
//import com.example.zchan_rtmp.ScreenActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'bilibili' library on application startup.
    static {
        System.loadLibrary("bilibili");
    }

    private ActivityMainBinding binding;
    private String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        changeFragment(new HomeActivity());
        binding.bottomBar.setItemOnClickListener(new MainBottomBar.OnItemOnClickListener() {
            @Override
            public void onClick(int item) {
                Log.e(TAG, "onClick: " + item);
                switch (item) {
                    case 0:
                        changeFragment(new HomeActivity());
                        break;
                    case 1:
                        changeFragment(new DynamicActivity());
                        break;
                    case 2:
                        showPopupWindow(binding.bottomBar);
                        break;
                    case 3:
                        changeFragment(new CategoryActivity());
                        break;
                    case 4:
                        changeFragment(new CommunicateActivity());
                        break;
                }
            }
        });
    }

    public void showPopupWindow(View anchorView) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(com.example.view.R.layout.popup_window_layout, null);

        PopupWindow popupWindow = new PopupWindow(popupView,
                com.example.utils.SizeUtils.dip2px(this, 160),
                com.example.utils.SizeUtils.dip2px(this, 65));

        // Specify the popup window's background, necessary for the elevation and outline to show
        popupWindow.setBackgroundDrawable(getResources().getDrawable(com.example.view.R.drawable.popup_background));


        // Set elevation for Android Lollipop and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            popupWindow.setElevation(20);
        }

        // Dismiss the popup window when touched outside
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        int screenW = getResources().getDisplayMetrics().widthPixels;
        // Show the popup window
        popupWindow.showAsDropDown(anchorView, screenW / 2 - popupWindow.getWidth() / 2, -(anchorView.getHeight() + popupWindow.getHeight() + 40), Gravity.NO_GRAVITY);
        popupView.findViewById(com.example.view.R.id.btn_librtmp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, com.example.zchan_player_annotating.ui.PlayerActivity.class);
                popupWindow.dismiss();
                startActivity(intent);
            }
        });
        popupView.findViewById(com.example.view.R.id.btn_ffmpeg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ScreenActivity.class);
                popupWindow.dismiss();
                startActivity(intent);
            }
        });
    }

    private void changeFragment(Fragment fragment) {
        FragmentManager FRAGMENT_MANAGER = getSupportFragmentManager();
        FragmentTransaction TRANSACTION = FRAGMENT_MANAGER.beginTransaction();
        TRANSACTION.replace(R.id.fragmentView, fragment);
        TRANSACTION.commit();
    }

}