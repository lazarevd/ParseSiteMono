package ru.laz.common.models;

import java.io.Serializable;

public class NewsBlockDTO implements Serializable {
    private int id;
    private String date = "";
    private String title = "";
    private String url = "";
    private String body = "";

    private long createdTime = -1;

    public NewsBlockDTO() {}

    public NewsBlockDTO(long createdTime)  {
        this.createdTime = createdTime;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isExpired(long expiryTime) {
            if ((System.currentTimeMillis() - getCreatedTime()) < expiryTime) {
                return false;
            }
        return true;
    }
}
