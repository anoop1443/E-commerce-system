package com.example.homeadmin.ui.orders;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

public class QuickOrderModel {
    private String orderID, serviceName, price, userID, userName, userMobile, userAddress, orderStatus, paymentStatus;
    private String deliveryBoyName, deliveryBoyID;
    private String lastDeclinedByBoyID, lastDeclinedByBoyName, declineReason;
    private Timestamp dateTime;

    public QuickOrderModel() {} // Required for Firebase

    @PropertyName("orderID")
    public String getOrderID() { return orderID; }
    @PropertyName("orderID")
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    @PropertyName("userID")
    public String getUserID() { return userID; }
    @PropertyName("userID")
    public void setUserID(String userID) { this.userID = userID; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserMobile() { return userMobile; }
    public void setUserMobile(String userMobile) { this.userMobile = userMobile; }

    public String getUserAddress() { return userAddress; }
    public void setUserAddress(String userAddress) { this.userAddress = userAddress; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getDeliveryBoyName() {
        return deliveryBoyName;
    }

    public void setDeliveryBoyName(String deliveryBoyName) {
        this.deliveryBoyName = deliveryBoyName;
    }

    public String getDeliveryBoyID() {
        return deliveryBoyID;
    }

    public void setDeliveryBoyID(String deliveryBoyID) {
        this.deliveryBoyID = deliveryBoyID;
    }

    public Timestamp getDateTime() { return dateTime; }
    public void setDateTime(Timestamp dateTime) { this.dateTime = dateTime; }

    // Legacy support
    @Exclude
    public String getOrderId() { return getOrderID(); }
    @Exclude
    public void setOrderId(String id) { setOrderID(id); }
    @Exclude
    public String getUserId() { return getUserID(); }
    @Exclude
    public void setUserId(String id) { setUserID(id); }

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
