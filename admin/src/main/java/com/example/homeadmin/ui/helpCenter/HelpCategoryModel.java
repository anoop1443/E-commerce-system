package com.example.homeadmin.ui.helpCenter;

public class HelpCategoryModel {
    private String categoryId;
    private String name;
    private String icon;
    private String color;

    public HelpCategoryModel() {
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

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
