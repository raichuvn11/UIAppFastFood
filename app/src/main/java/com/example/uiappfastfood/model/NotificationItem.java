package com.example.uiappfastfood.model;

public class NotificationItem {
    private int iconResId;
    private String title;
    private String description;
    private long timeStamp;

    private boolean isRead;

    public NotificationItem(int iconResId, String title, String description, long timeStamp) {
        this.iconResId = iconResId;
        this.title = title;
        this.description = description;
        this.timeStamp = timeStamp;
        this.isRead = false;
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
}
