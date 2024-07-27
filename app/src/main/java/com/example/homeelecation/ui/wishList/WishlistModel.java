package com.example.homeelecation.ui.wishList;

public class WishlistModel {

    private  String ProductID;
    private String productImage;
    private long FreeCoupon;
    private double starRating;
    private long TotalRating;
    private String ProductTitle;
    private long Prise;
    private long CatPrise;
    private String PaymentMethod;


    public WishlistModel(String productID,String productImage, long freeCoupon, double starRating, long totalRating, String productTitle, long prise, long catPrise, String paymentMethod) {
        this.ProductID = productID;
        this.productImage = productImage;
        FreeCoupon = freeCoupon;
        this.starRating = starRating;
        TotalRating = totalRating;
        ProductTitle = productTitle;
        Prise = prise;
        CatPrise = catPrise;
        PaymentMethod = paymentMethod;

    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public long getFreeCoupon() {
        return FreeCoupon;
    }

    public void setFreeCoupon(long freeCoupon) {
        FreeCoupon = freeCoupon;
    }

    public double getStarRating() {
        return starRating;
    }

    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }

    public long getTotalRating() {
        return TotalRating;
    }

    public void setTotalRating(long totalRating) {
        TotalRating = totalRating;
    }

    public String getProductTitle() {
        return ProductTitle;
    }

    public void setProductTitle(String productTitle) {
        ProductTitle = productTitle;
    }

    public long getPrise() {
        return Prise;
    }

    public void setPrise(long prise) {
        Prise = prise;
    }

    public long getCatPrise() {
        return CatPrise;
    }

    public void setCatPrise(long catPrise) {
        CatPrise = catPrise;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }


}


