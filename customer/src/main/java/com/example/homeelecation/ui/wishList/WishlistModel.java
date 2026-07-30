package com.example.homeelecation.ui.wishList;

import com.google.firebase.firestore.PropertyName;
import java.util.ArrayList;

public class WishlistModel {

    private String productID;
    private String productImage;
    private double freeCoupon;
    private double starRating;
    private long totalRatings;
    private String productTitle;
    private long productPrice;
    private long cutPrice;
    private String paymentMethod;
    private ArrayList<String> tags;

    public WishlistModel() {
        // Required for Firestore
    }

    public WishlistModel(String productID, String productImage, double freeCoupon, double starRating, long totalRatings, String productTitle, long productPrice, long cutPrice, String paymentMethod) {
        this.productID = productID;
        this.productImage = productImage;
        this.freeCoupon = freeCoupon;
        this.starRating = starRating;
        this.totalRatings = totalRatings;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.cutPrice = cutPrice;
        this.paymentMethod = paymentMethod;
    }

    @PropertyName("productID")
    public String getProductID() {
        return productID;
    }

    @PropertyName("productID")
    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public double getFreeCoupon() {
        return freeCoupon;
    }

    public void setFreeCoupon(double freeCoupon) {
        this.freeCoupon = freeCoupon;
    }

    public double getStarRating() {
        return starRating;
    }

    public void setStarRating(double starRating) {
        this.starRating = starRating;
    }

    @PropertyName("totalRatings")
    public long getTotalRatings() {
        return totalRatings;
    }

    @PropertyName("totalRatings")
    public void setTotalRatings(long totalRatings) {
        this.totalRatings = totalRatings;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    @PropertyName("productPrice")
    public long getProductPrice() {
        return productPrice;
    }

    @PropertyName("productPrice")
    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    @PropertyName("cutPrice")
    public long getCutPrice() {
        return cutPrice;
    }

    @PropertyName("cutPrice")
    public void setCutPrice(long cutPrice) {
        this.cutPrice = cutPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    // Legacy support for older code or Firestore case mismatches
    @PropertyName("productId")
    public String getProductId() { return getProductID(); }
    @PropertyName("productId")
    public void setProductId(String id) { setProductID(id); }
    @PropertyName("product_ID")
    public String getProductID_Alt() { return getProductID(); }
    @PropertyName("product_ID")
    public void setProductID_Alt(String id) { setProductID(id); }

    public long getPrise() { return getProductPrice(); }
    public void setPrise(long price) { setProductPrice(price); }
    public long getCatPrise() { return getCutPrice(); }
    public void setCatPrise(long price) { setCutPrice(price); }
    public long getTotalRating() { return getTotalRatings(); }
    public void setTotalRating(long rating) { setTotalRatings(rating); }
}
