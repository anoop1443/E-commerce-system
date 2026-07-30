package com.example.deliveryboy.order;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class OrderModel {

    private String orderID;
    private String customerName;
    private String customerAddress;
    private String orderStatus;
    private String deliveryBoyID;
    private String lastDeclinedByBoyID;
    private String lastDeclinedByBoyName;
    private String declineReason;

    public OrderModel() {
        // Default constructor zaroori hai Firestore ke liye
    }

    public OrderModel(String orderID, String customerName, String customerAddress, String orderStatus, String deliveryBoyID) {
        this.orderID = orderID;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.orderStatus = orderStatus;
        this.deliveryBoyID = deliveryBoyID;
    }

    @PropertyName("orderID")
    public String getOrderID() {
        return orderID;
    }

    @PropertyName("orderID")
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @PropertyName("deliveryBoyID")
    public String getDeliveryBoyID() {
        return deliveryBoyID;
    }

    @PropertyName("deliveryBoyID")
    public void setDeliveryBoyID(String deliveryBoyID) {
        this.deliveryBoyID = deliveryBoyID;
    }

    // Legacy support
    @Exclude
    public String getOrderId() { return getOrderID(); }
    @Exclude
    public void setOrderId(String id) { setOrderID(id); }
    @Exclude
    public String getDeliveryBoyId() { return getDeliveryBoyID(); }
    @Exclude
    public void setDeliveryBoyId(String id) { setDeliveryBoyID(id); }

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
