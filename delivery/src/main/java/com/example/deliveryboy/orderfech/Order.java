package com.example.deliveryboy.orderfech;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.io.Serializable;

public class Order implements Serializable {

    private String deliveryBoyID;
    @DocumentId
    private String orderID;
    private String productID;
    private String productTitle;
    private String status;
    private String imageUrl;
    private String customerAddress;
    private String fullName;
    private String mobile;
    private boolean isQuickOrder = false;
    private String lastDeclinedByBoyID;
    private String lastDeclinedByBoyName;
    private String declineReason;

    public Order() {
        // Default constructor is required for Firestore
    }

    public String getDeliveryBoyID() {
        return deliveryBoyID;
    }

    public void setDeliveryBoyID(String deliveryBoyID) {
        this.deliveryBoyID = deliveryBoyID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public boolean isQuickOrder() {
        return isQuickOrder;
    }

    public void setQuickOrder(boolean quickOrder) {
        isQuickOrder = quickOrder;
    }

    public String getLastDeclinedByBoyID() {
        return lastDeclinedByBoyID;
    }

    public void setLastDeclinedByBoyID(String lastDeclinedByBoyID) {
        this.lastDeclinedByBoyID = lastDeclinedByBoyID;
    }

    public String getLastDeclinedByBoyName() {
        return lastDeclinedByBoyName;
    }

    public void setLastDeclinedByBoyName(String lastDeclinedByBoyName) {
        this.lastDeclinedByBoyName = lastDeclinedByBoyName;
    }

    public String getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(String declineReason) {
        this.declineReason = declineReason;
    }
}