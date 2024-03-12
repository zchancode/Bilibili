package com.example.bilibili.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bilibili.R;
import com.example.bilibili.service.CreateLiveTask;
import com.example.bilibili.service.ImageUploader;
import com.example.bilibili.utils.ImagePicker;
import com.example.zchan_rtmp.CameraXActivity;
import com.squareup.picasso.Picasso;

public class CreateRoomActivity extends AppCompatActivity {
    private ImagePicker imagePicker;

    private CreateLiveTask mCreateLiveTask;
    private TextView mUploadCoverText;
    private ImageView mImageView;
    private ConstraintLayout mCoverLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        EditText roomNum = findViewById(R.id.room_number);
        EditText roomName = findViewById(R.id.room_name);

        imagePicker = new ImagePicker(this);
        findViewById(R.id.upload_cover_layout).setOnClickListener(v -> {
            imagePicker.openImageChooser();
        });
        mCoverLayout = findViewById(R.id.upload_cover_layout);
        mUploadCoverText = findViewById(R.id.upload_cover_text);
        mImageView = findViewById(R.id.add_pic_btn);
        findViewById(R.id.btn_start).setOnClickListener(v -> {
            mCreateLiveTask = new CreateLiveTask();
            mCreateLiveTask.setResponseListener(response -> {
                if (response != null) {
                    Log.d("CreateRoomActivity", response);
                }
            });
            mCreateLiveTask.execute("http://172.20.10.2/bilibili/api.php?api=createLive&roomName=" + roomName.getText().toString() + "&roomUpName=upname&roomPic=pic&roomUrl=url");
            Intent intent = new Intent(this, CameraXActivity.class);
            intent.putExtra("url", "rtmp://172.20.10.2:1935/" + roomNum.getText().toString() + "/hls");
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ImagePicker.PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            String imagePath = imagePicker.getPath(uri);
            ImageUploader imageUploader = new ImageUploader(this);
            imageUploader.setResponseListener(response -> {
                if (response != null) {
                    Toast.makeText(this, "Upload success:" + response, Toast.LENGTH_SHORT).show();
                    mImageView.setVisibility(View.GONE);
                    mUploadCoverText.setText("不满意？重新上传");
                    mCoverLayout.setBackground(Drawable.createFromPath(imagePath));

                }
            });
            imageUploader.uploadImage(imagePath, "http://172.20.10.2/bilibili/api.php?api=uploadPic");

        }
    }
}