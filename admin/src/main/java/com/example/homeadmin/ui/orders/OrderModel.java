package com.example.homeadmin.ui.orders;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class OrderModel {
    private String orderID,globalStatus, fullName, address, mobile, orderStatus, userID, deliveryBoyName;
    private String lastDeclinedByBoyID, lastDeclinedByBoyName, declineReason;
    private Object totalAmount;
    private Timestamp date;
    // Add other fields as per your Firebase

    public OrderModel() {} // Required for Firebase

    public OrderModel(String orderID, String globalStatus, String fullName, String address, String mobile, String orderStatus, String userID, String deliveryBoyName, Object totalAmount) {
        this.orderID = orderID;
        this.globalStatus = globalStatus;
        this.fullName = fullName;
        this.address = address;
        this.mobile = mobile;
        this.orderStatus = orderStatus;
        this.userID = userID;
        this.deliveryBoyName = deliveryBoyName;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters...



    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Object getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Object totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDeliveryBoyName(){
        return deliveryBoyName;
    }

    public void setDeliveryBoyName(String deliveryBoyName) {
        this.deliveryBoyName = deliveryBoyName;
    }

    @PropertyName("globalStatus")
    public String getGlobalStatus() {
        return globalStatus;
    }

    @PropertyName("globalStatus")
    public void setGlobalStatus(String globalStatus) {
        this.globalStatus = globalStatus;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
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
