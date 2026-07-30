package com.example.homeelecation.ui.orders;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class QuickOrderModel {
    private String orderID;
    private String serviceName;
    private String serviceImage;
    private String price;
    private String userID;
    private String userName;
    private String userMobile;
    private String userAddress;
    private String orderStatus;
    private String paymentStatus;
    private Date dateTime;
    private String lastDeclinedByBoyID;
    private String lastDeclinedByBoyName;
    private String declineReason;

    public QuickOrderModel() {
    }

    public String getOrderID() { return orderID; }

    public void setOrderID(String orderID) { this.orderID = orderID; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getServiceImage() { return serviceImage; }
    public void setServiceImage(String serviceImage) { this.serviceImage = serviceImage; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public String getUserID() { return userID; }

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

    public Date getDateTime() { return dateTime; }
    public void setDateTime(Date dateTime) { this.dateTime = dateTime; }

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
