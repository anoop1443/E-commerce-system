package com.example.homeelecation.ui.horizontal;

public class HorizontalProductScrollModel {

    private String ProductId;
    private String productImage;
    private String productTitle;
    private String productDescription;
    private String productPrice;

    public HorizontalProductScrollModel(String productId, String productImage, String producttitel, String productdescription, String productprice) {
        this.ProductId = productId;
        this.productImage = productImage;
        this.productTitle = producttitel;
        this.productDescription = productdescription;
        this.productPrice = productprice;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
       this.ProductId = productId;
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
