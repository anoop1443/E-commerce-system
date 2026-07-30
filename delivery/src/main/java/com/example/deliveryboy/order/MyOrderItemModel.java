package com.example.deliveryboy.order;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MyOrderItemModel {

    private String productID;
    private String orderID;
    private String productTitle;
    private String productImage;
    private String orderStatus;
    private String refundStatus;
    private Date orderedDate;
    private Date packedDate;
    private Date shippedDate;
    private Date deliveredDate;
    private Date cancelledDate;
    private Date refundDate;
    private String fullName;
    private String address;
    private String mobile;
    private String pinCode;
    private String productPrice;
    private String cutPrice;
    private String userID;
    private String paymentMethod;
    private long quantity;
    private int rating = 0;
    private String deliveryCharge;
    private boolean cancellationRequested;

    // Dynamic Body Text
    private String orderedTitle, orderedBody;
    private String packedTitle, packedBody;
    private String shippedTitle, shippedBody;
    private String deliveredTitle, deliveredBody;

    private List<Map<String, Object>> billItems;
    private String finalTotal;

    public MyOrderItemModel() {
    }

    // Standard Properties


    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Date getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(Date orderedDate) {
        this.orderedDate = orderedDate;
    }

    public Date getPackedDate() {
        return packedDate;
    }

    public void setPackedDate(Date packedDate) {
        this.packedDate = packedDate;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
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

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(String cutPrice) {
        this.cutPrice = cutPrice;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public boolean isCancellationRequested() {
        return cancellationRequested;
    }

    public void setCancellationRequested(boolean cancellationRequested) {
        this.cancellationRequested = cancellationRequested;
    }

    public String getOrderedTitle() {
        return orderedTitle;
    }

    public void setOrderedTitle(String orderedTitle) {
        this.orderedTitle = orderedTitle;
    }

    public String getOrderedBody() {
        return orderedBody;
    }

    public void setOrderedBody(String orderedBody) {
        this.orderedBody = orderedBody;
    }

    public String getPackedTitle() {
        return packedTitle;
    }

    public void setPackedTitle(String packedTitle) {
        this.packedTitle = packedTitle;
    }

    public String getPackedBody() {
        return packedBody;
    }

    public void setPackedBody(String packedBody) {
        this.packedBody = packedBody;
    }

    public String getShippedTitle() {
        return shippedTitle;
    }

    public void setShippedTitle(String shippedTitle) {
        this.shippedTitle = shippedTitle;
    }

    public String getShippedBody() {
        return shippedBody;
    }

    public void setShippedBody(String shippedBody) {
        this.shippedBody = shippedBody;
    }

    public String getDeliveredTitle() {
        return deliveredTitle;
    }

    public void setDeliveredTitle(String deliveredTitle) {
        this.deliveredTitle = deliveredTitle;
    }

    public String getDeliveredBody() {
        return deliveredBody;
    }

    public void setDeliveredBody(String deliveredBody) {
        this.deliveredBody = deliveredBody;
    }

    public List<Map<String, Object>> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<Map<String, Object>> billItems) {
        this.billItems = billItems;
    }

    public String getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(String finalTotal) {
        this.finalTotal = finalTotal;
    }
}