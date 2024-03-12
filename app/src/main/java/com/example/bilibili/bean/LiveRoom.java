package com.example.bilibili.bean;

/**
 * Created by Mr.Chan
 * Time 2024-03-12
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class LiveRoom {
    private String roomName;
    private String roomUpName;
    private String roomPic;
    private String roomUrl;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomUpName() {
        return roomUpName;
    }

    public void setRoomUpName(String roomUpName) {
        this.roomUpName = roomUpName;
    }

    public String getRoomPic() {
        return roomPic;
    }

    public void setRoomPic(String roomPic) {
        this.roomPic = roomPic;
    }

    public String getRoomUrl() {
        return roomUrl;
    }

    public void setRoomUrl(String roomUrl) {
        this.roomUrl = roomUrl;
    }

    @Override
    public String toString() {
        return "LiveRoom{" +
                "roomName='" + roomName + '\'' +
                ", roomUpName='" + roomUpName + '\'' +
                ", roomPic='" + roomPic + '\'' +
                ", roomUrl='" + roomUrl + '\'' +
                '}';
    }
}
