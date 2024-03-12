package com.example.bilibili.service;

import android.os.AsyncTask;
import android.util.Log;

import com.example.bilibili.bean.LiveRoom;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class FetchLivesTask extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... urls) {
        try {
            FetchLivesService service = new FetchLivesService();
            return service.getLives(urls[0]);
        } catch (IOException e) {
            Log.e("FetchLivesTask", "Error fetching lives", e);
            e.printStackTrace();
            return null;
        }
    }

    private ResponseListener listener;

    public interface ResponseListener {
        void onResponse(LiveRoom liveRoom);
    }

    public void setResponseListener(ResponseListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (listener != null) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    LiveRoom liveRoom = new LiveRoom();
                    liveRoom.setRoomName(jsonObject.getString("roomName"));
                    liveRoom.setRoomUpName(jsonObject.getString("roomUpName"));
                    liveRoom.setRoomPic(jsonObject.getString("roomPic"));
                    liveRoom.setRoomUrl(jsonObject.getString("roomUrl"));
                    listener.onResponse(liveRoom);
                }
            } catch (Exception e) {
                Log.e("FetchLivesTask", "Error parsing JSON", e);
            }
        }
    }
}
