package com.example.homeadmin.user;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class OrderItem {
    @ServerTimestamp
    private Date Time;
    @ServerTimestamp
    private Date cancelledDate;
    @ServerTimestamp
    private Date deliveredDate;
    @ServerTimestamp
    private Date orderedDate;
    @ServerTimestamp
    private Date packedDate;
    @ServerTimestamp
    private Date refundDate;
    @ServerTimestamp
    private Date shippedDate;

    private String address, cutPrice, fullName, mobile, orderID, orderMonth, orderStatus, orderYear, paymentMethod, pinCode, productID, productImage, productPrice, refundStatus, userID, productTitle;
    private boolean cancellationRequested;
    private long productQuantity;

    public OrderItem() {
    }

    public Date getTime() {
        return Time;
    }

    public void setTime(Date time) {
        Time = time;
    }

    @PropertyName("address")
    public String getAddress() {
        return address;
    }

    @PropertyName("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @Exclude
    public String getAddressSpaced() {
        return address;
    }

    @Exclude
    public void setAddressSpaced(String address) {
        this.address = address;
    }

    public Date getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(Date cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(String cutPrice) {
        this.cutPrice = cutPrice;
    }

    public Date getDeliveredDate() {
        return deliveredDate;
    }

    public void setDeliveredDate(Date deliveredDate) {
        this.deliveredDate = deliveredDate;
    }

    @PropertyName("fullName")
    public String getFullName() {
        return fullName;
    }

    @PropertyName("fullName")
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @PropertyName("Full name")
    public String getFullNameSpaced() {
        return fullName;
    }

    @PropertyName("Full name")
    public void setFullNameSpaced(String name) {
        this.fullName = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @PropertyName("orderID")
    public String getOrderID() {
        return orderID;
    }

    @PropertyName("orderID")
    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    @PropertyName("Order id")
    public String getOrderIDSpaced() {
        return orderID;
    }

    @PropertyName("Order id")
    public void setOrderIDSpaced(String id) {
        this.orderID = id;
    }

    public String getOrderMonth() {
        return orderMonth;
    }

    public void setOrderMonth(String orderMonth) {
        this.orderMonth = orderMonth;
    }

    @PropertyName("orderStatus")
    public String getOrderStatus() {
        return orderStatus;
    }

    @PropertyName("orderStatus")
    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    @PropertyName("Order status")
    public String getOrderStatusSpaced() {
        return orderStatus;
    }

    @PropertyName("Order status")
    public void setOrderStatusSpaced(String status) {
        this.orderStatus = status;
    }

    public String getOrderYear() {
        return orderYear;
    }

    public void setOrderYear(String orderYear) {
        this.orderYear = orderYear;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    @PropertyName("productID")
    public String getProductID() {
        return productID;
    }

    @PropertyName("productID")
    public void setProductID(String productID) {
        this.productID = productID;
    }

    @PropertyName("Product id")
    public String getProductIDSpaced() {
        return productID;
    }

    @PropertyName("Product id")
    public void setProductIDSpaced(String id) {
        this.productID = id;
    }

    @PropertyName("productImage")
    public String getProductImage() {
        return productImage;
    }

    @PropertyName("productImage")
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    @PropertyName("Product image")
    public String getProductImageSpaced() {
        return productImage;
    }

    @PropertyName("Product image")
    public void setProductImageSpaced(String image) {
        this.productImage = image;
    }

    @PropertyName("productPrice")
    public String getProductPrice() {
        return productPrice;
    }

    @PropertyName("productPrice")
    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    @PropertyName("Product price")
    public String getProductPriceSpaced() {
        return productPrice;
    }

    @PropertyName("Product price")
    public void setProductPriceSpaced(String price) {
        this.productPrice = price;
    }

    public Date getRefundDate() {
        return refundDate;
    }

    public void setRefundDate(Date refundDate) {
        this.refundDate = refundDate;
    }

    public String getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(String refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Date getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(Date shippedDate) {
        this.shippedDate = shippedDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @PropertyName("productTitle")
    public String getProductTitle() {
        return productTitle;
    }

    @PropertyName("productTitle")
    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    @PropertyName("Product title")
    public String getProductTitleSpaced() {
        return productTitle;
    }

    @PropertyName("Product title")
    public void setProductTitleSpaced(String title) {
        this.productTitle = title;
    }

    public boolean isCancellationRequested() {
        return cancellationRequested;
    }

    public void setCancellationRequested(boolean cancellationRequested) {
        this.cancellationRequested = cancellationRequested;
    }

    public long getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(long productQuantity) {
        this.productQuantity = productQuantity;
    }
}
