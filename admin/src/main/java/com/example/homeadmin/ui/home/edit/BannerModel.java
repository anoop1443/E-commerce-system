package com.example.homeadmin.ui.home.edit;

public class BannerModel {
    private String documentId;
    private String imageUrl;
    private String backgroundColor;
    private boolean isSelected = false;

    public BannerModel(String documentId, String imageUrl, String backgroundColor) {
        this.documentId = documentId;
        this.imageUrl = imageUrl;
        this.backgroundColor = backgroundColor;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
