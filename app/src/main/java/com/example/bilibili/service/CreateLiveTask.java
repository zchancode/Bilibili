package com.example.bilibili.service;

import android.os.AsyncTask;

import java.io.IOException;

public class CreateLiveTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            LivesService service = new LivesService();
            return service.getLives(urls[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private ResponseListener listener;

    public interface ResponseListener {
        void onResponse(String liveRoom);
    }

    public void setResponseListener(ResponseListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            listener.onResponse(result);
        }
    }
}
