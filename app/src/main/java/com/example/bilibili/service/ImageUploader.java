package com.example.bilibili.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ImageUploader {
    
    private Context context;

    public ImageUploader(Context context) {
        this.context = context;
    }

    private ResponseListener responseListener;
    public void setResponseListener(ResponseListener responseListener) {
        this.responseListener = responseListener;
    }
    public interface ResponseListener {
        void onResponse(String response);
    }
    public void uploadImage(String imagePath, String uploadUrl) {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        final byte[] imageBytes = byteArrayOutputStream.toByteArray();
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (responseListener != null) {
                            responseListener.onResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ImageUploader", "Error: " + error.getMessage());
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", Base64.encodeToString(imageBytes, Base64.DEFAULT));
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
}
