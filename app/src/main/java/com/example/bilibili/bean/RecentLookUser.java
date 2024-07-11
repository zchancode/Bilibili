package com.example.bilibili.bean;

/**
 * Created by Mr.Chan
 * Time 2024-04-10
 * Blog https://www.cnblogs.com/Frank-dev-blog/
 */
public class RecentLookUser {
    private String name;
    private String avatar;

    public RecentLookUser(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "RecentLookUser{" +
                "name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
