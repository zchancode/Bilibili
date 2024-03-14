package com.example.bilibili.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.bilibili.R;
import com.example.bilibili.databinding.ActivityMainBinding;
import com.example.viewgroup.MainBottomBar;
import com.example.zchan_orgrtmp.ui.OrgRTMPActivity;
import com.example.zchan_rtmp.CameraXActivity;
import com.example.zchan_rtmp.InnerSoundService;
import com.example.zchan_rtmp.ScreenActivity;

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
                        startActivity(new Intent(MainActivity.this, OrgRTMPActivity.class));
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

    private void changeFragment(Fragment fragment) {
        FragmentManager FRAGMENT_MANAGER = getSupportFragmentManager();
        FragmentTransaction TRANSACTION = FRAGMENT_MANAGER.beginTransaction();
        TRANSACTION.replace(R.id.fragmentView, fragment);
        TRANSACTION.commit();
    }
    /**
     * A native method that is implemented by the 'bilibili' native library,
     * which is packaged with this application.
     */
}