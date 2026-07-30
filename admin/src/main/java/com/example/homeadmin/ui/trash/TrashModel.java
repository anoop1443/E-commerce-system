package com.example.homeadmin.ui.trash;

import java.util.Date;
import java.util.Map;

public class TrashModel {
    private String trashId;
    private String originalId;
    private String originalCollection;
    private Map<String, Object> data;
    private Date deletedAt;
    private Date expiryDate;
    private String type; // e.g., "PRODUCT", "CATEGORY", "BANNER", "AD", "LAYOUT_SECTION", "QUICK_SERVICE"
    private String label; // Human readable name
    private String imageUrl; // For preview

    public TrashModel() {
    }

    public TrashModel(String trashId, String originalId, String originalCollection, Map<String, Object> data, Date deletedAt, Date expiryDate, String type, String label, String imageUrl) {
        this.trashId = trashId;
        this.originalId = originalId;
        this.originalCollection = originalCollection;
        this.data = data;
        this.deletedAt = deletedAt;
        this.expiryDate = expiryDate;
        this.type = type;
        this.label = label;
        this.imageUrl = imageUrl;
    }

    public String getTrashId() {
        return trashId;
    }

    public void setTrashId(String trashId) {
        this.trashId = trashId;
    }

    public String getOriginalId() {
        return originalId;
    }

    public void setOriginalId(String originalId) {
        this.originalId = originalId;
    }

    public String getOriginalCollection() {
        return originalCollection;
    }

    public void setOriginalCollection(String originalCollection) {
        this.originalCollection = originalCollection;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
