package com.example.homeelecation.ui.categoryView;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class CategoryModel {
    private String categoryID;
    private String categoryIconLink;
    private String categoryName;
    private long index;

    public CategoryModel() {
        // Required for Firestore
    }

    public CategoryModel(String categoryID, String categoryIconLink, String categoryName) {
        this.categoryID = categoryID;
        this.categoryIconLink = categoryIconLink;
        this.categoryName = categoryName;
    }

    public CategoryModel(String categoryID, String categoryIconLink, String categoryName, long index) {
        this.categoryID = categoryID;
        this.categoryIconLink = categoryIconLink;
        this.categoryName = categoryName;
        this.index = index;
    }

    @PropertyName("categoryID")
    public String getCategoryID() {
        return categoryID;
    }

    @PropertyName("categoryID")
    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    @PropertyName("icon")
    public String getCategoryIconLink() {
        return categoryIconLink;
    }

    @PropertyName("icon")
    public void setCategoryIconLink(String categoryIconLink) {
        this.categoryIconLink = categoryIconLink;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    // Legacy support for id/categoryId case mismatches
    @Exclude
    public String getCategoryId() { return getCategoryID(); }
    @Exclude
    public void setCategoryId(String id) { setCategoryID(id); }
    @Exclude
    public String getID() { return getCategoryID(); }
    @Exclude
    public void setID(String id) { setCategoryID(id); }
}
