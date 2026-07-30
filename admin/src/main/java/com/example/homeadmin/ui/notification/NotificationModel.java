package com.example.homeadmin.ui.notification;

public class NotificationModel {
    private String image;
    private String textview;

    private boolean read;

    public NotificationModel(String image, String textview, boolean read) {
        this.image = image;
        this.textview = textview;
        this.read = read;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTextview() {
        return textview;
    }

    public void setTextview(String textview) {
        this.textview = textview;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
