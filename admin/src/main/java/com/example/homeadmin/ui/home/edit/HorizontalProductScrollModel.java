package com.example.homeadmin.ui.home.edit;

public class HorizontalProductScrollModel {

    private String documentId;
    private String productImage;
    private String productTitle;
    private String productDescription;
    private String productPrice;
    private boolean isSelected;

    public HorizontalProductScrollModel() {
    }

    public HorizontalProductScrollModel(String documentId, String productImage, String productTitle, String productDescription, String productPrice) {
        this.documentId = documentId;
        this.productImage = productImage;
        this.productTitle = productTitle;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.isSelected = false; // Default to not selected
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }
}
