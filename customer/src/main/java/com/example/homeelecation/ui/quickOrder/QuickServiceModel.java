package com.example.homeelecation.ui.quickOrder;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class QuickServiceModel {
    @DocumentId
    private String documentId;
    private String name;
    private String price;
    private String category;
    private String description;
    private String rules;
    private String icon;
    private String color;
    private int index;
    private boolean available;
    
    @ServerTimestamp
    private Date dateTime;

    public QuickServiceModel() {
    }

    public QuickServiceModel(String documentId, String name, String price, String category, String description, String rules, String icon, String color, int index, boolean available, Date dateTime) {
        this.documentId = documentId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.description = description;
        this.rules = rules;
        this.icon = icon;
        this.color = color;
        this.index = index;
        this.available = available;
        this.dateTime = dateTime;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public Date getDateTime() { return dateTime; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }
}
