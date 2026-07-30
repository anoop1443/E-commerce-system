package com.example.homeadmin.ui.slideshow;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class SliderModel {
    private String documentID;
    private String banner;
    private String backgroundColor;

    public SliderModel(String documentID, String banner, String backgroundColor) {
        this.documentID = documentID;
        this.banner = banner;
        this.backgroundColor = backgroundColor;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @PropertyName("documentID")
    public String getDocumentID() {
        return documentID;
    }

    @PropertyName("documentID")
    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    // Legacy support
    @Exclude
    public String getDocumentId() { return getDocumentID(); }
    @Exclude
    public void setDocumentId(String id) { setDocumentID(id); }
    @PropertyName("backGroundColor")
    public String getBackGroundColor() { return getBackgroundColor(); }
    @PropertyName("backGroundColor")
    public void setBackGroundColor(String color) { setBackgroundColor(color); }
}
