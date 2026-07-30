package com.example.homeelecation.ui.profile;

public class HelpCategoryModel {
    private String categoryId;
    private String name;
    private String icon;
    private String color;

    public HelpCategoryModel() {
        // Required for Firebase
    }

    public HelpCategoryModel(String categoryId, String name, String icon, String color) {
        this.categoryId = categoryId;
        this.name = name;
        this.icon = icon;
        this.color = color;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }
}
