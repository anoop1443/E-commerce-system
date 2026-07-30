package com.example.homeadmin.ui.quickServices;

public class QuickServiceModel {
    private String id;
    private String name;
    private String price;
    private String color;
    private String icon;
    private String rules;
    private String description; // Short detail about the service
    private String category;    // To group services like Plumbing, Electrical, etc.
    private int index;
    private boolean available;

    public QuickServiceModel() {
    }

    public QuickServiceModel(String name, String price, String color, String icon, String rules, String description, String category, int index, boolean available) {
        this.name = name;
        this.price = price;
        this.color = color;
        this.icon = icon;
        this.rules = rules;
        this.description = description;
        this.category = category;
        this.index = index;
        this.available = available;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
