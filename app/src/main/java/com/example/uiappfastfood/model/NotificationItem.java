package com.example.uiappfastfood.model;

public class NotificationItem {
    private int iconResId;
    private String title;
    private String description;
    private long timeStamp;

    private boolean isRead;
    private String type;

    public NotificationItem(int iconResId, String title, String description, long timeStamp, String type) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
        this.isRead = false;
        this.type = type;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
