package com.example.homeadmin.ui.home.edit;

public class AdModel {
    private String documentId;
    private String imageUrl;
    private String backgroundColor;

    public AdModel(String documentId, String imageUrl, String backgroundColor) {
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
}
