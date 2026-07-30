package com.example.homeelecation.ui.orders;

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
    private long otp;

    // Dynamic Body Text
    private String orderedTitle, orderedBody;
    private String packedTitle, packedBody;
    private String shippedTitle, shippedBody;
    private String deliveredTitle, deliveredBody;

    private List<Map<String, Object>> billItems;
    private String finalTotal;

    public MyOrderItemModel() {
    }

    public MyOrderItemModel(String productID, String orderID, String productTitle, String productImage, String orderStatus, Date orderedDate, Date packedDate, Date shippedDate, Date deliveredDate, Date cancelledDate, String fullName, String address, String mobile, String pinCode, String productPrice, String cutPrice, String userID, String paymentMethod, long quantity, String deliveryCharge, boolean cancellationRequested) {
        this.productID = productID;
        this.orderID = orderID;
        this.productTitle = productTitle;
        this.productImage = productImage;
        this.orderStatus = orderStatus;
        this.orderedDate = orderedDate;
        this.packedDate = packedDate;
        this.shippedDate = shippedDate;
        this.deliveredDate = deliveredDate;
        this.cancelledDate = cancelledDate;
        this.fullName = fullName;
        this.address = address;
        this.mobile = mobile;
        this.pinCode = pinCode;
        this.productPrice = productPrice;
        this.cutPrice = cutPrice;
        this.userID = userID;
        this.paymentMethod = paymentMethod;
        this.quantity = quantity;
        this.deliveryCharge = deliveryCharge;
        this.cancellationRequested = cancellationRequested;
    }

    @PropertyName("productID")
    public String getProductID() { return productID; }
    @PropertyName("productID")
    public void setProductID(String productID) { this.productID = productID; }

    @PropertyName("orderID")
    public String getOrderID() { return orderID; }
    @PropertyName("orderID")
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public String getRefundStatus() { return refundStatus; }
    public void setRefundStatus(String refundStatus) { this.refundStatus = refundStatus; }

    public Date getOrderedDate() { return orderedDate; }
    public void setOrderedDate(Date orderedDate) { this.orderedDate = orderedDate; }

    public Date getPackedDate() { return packedDate; }
    public void setPackedDate(Date packedDate) { this.packedDate = packedDate; }

    public Date getShippedDate() { return shippedDate; }
    public void setShippedDate(Date shippedDate) { this.shippedDate = shippedDate; }

    public Date getDeliveredDate() { return deliveredDate; }
    public void setDeliveredDate(Date deliveredDate) { this.deliveredDate = deliveredDate; }

    public Date getCancelledDate() { return cancelledDate; }
    public void setCancelledDate(Date cancelledDate) { this.cancelledDate = cancelledDate; }

    public Date getRefundDate() { return refundDate; }
    public void setRefundDate(Date refundDate) { this.refundDate = refundDate; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    @PropertyName("address")
    public String getAddress() { return address; }
    @PropertyName("address")
    public void setAddress(String address) { this.address = address; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }

    public String getProductPrice() { return productPrice; }
    public void setProductPrice(String productPrice) { this.productPrice = productPrice; }

    @PropertyName("cutPrice")
    public String getCutPrice() { return cutPrice; }
    @PropertyName("cutPrice")
    public void setCutPrice(String cutPrice) { this.cutPrice = cutPrice; }

    @PropertyName("userID")
    public String getUserID() { return userID; }
    @PropertyName("userID")
    public void setUserID(String userID) { this.userID = userID; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getDeliveryCharge() { return deliveryCharge; }
    public void setDeliveryCharge(String deliveryCharge) { this.deliveryCharge = deliveryCharge; }

    public boolean isCancellationRequested() { return cancellationRequested; }
    public void setCancellationRequested(boolean cancellationRequested) { this.cancellationRequested = cancellationRequested; }

    public String getOrderedTitle() { return orderedTitle; }
    public void setOrderedTitle(String orderedTitle) { this.orderedTitle = orderedTitle; }

    public String getOrderedBody() { return orderedBody; }
    public void setOrderedBody(String orderedBody) { this.orderedBody = orderedBody; }

    public String getPackedTitle() { return packedTitle; }
    public void setPackedTitle(String packedTitle) { this.packedTitle = packedTitle; }

    public String getPackedBody() { return packedBody; }
    public void setPackedBody(String packedBody) { this.packedBody = packedBody; }

    public String getShippedTitle() { return shippedTitle; }
    public void setShippedTitle(String shippedTitle) { this.shippedTitle = shippedTitle; }

    public String getShippedBody() { return shippedBody; }
    public void setShippedBody(String shippedBody) { this.shippedBody = shippedBody; }

    public String getDeliveredTitle() { return deliveredTitle; }
    public void setDeliveredTitle(String deliveredTitle) { this.deliveredTitle = deliveredTitle; }

    public String getDeliveredBody() { return deliveredBody; }
    public void setDeliveredBody(String deliveredBody) { this.deliveredBody = deliveredBody; }

    public List<Map<String, Object>> getBillItems() { return billItems; }
    public void setBillItems(List<Map<String, Object>> billItems) { this.billItems = billItems; }

    public String getFinalTotal() { return finalTotal; }
    public void setFinalTotal(String finalTotal) { this.finalTotal = finalTotal; }

    public long getOtp() { return otp; }
    public void setOtp(long otp) { this.otp = otp; }

    // Backward compatibility for case mismatches if any
    @Exclude
    public String getProductId() { return getProductID(); }
    @Exclude
    public void setProductId(String id) { setProductID(id); }
    @Exclude
    public String getOrderId() { return getOrderID(); }
    @Exclude
    public void setOrderId(String id) { setOrderID(id); }
    @Exclude
    public String getUserId() { return getUserID(); }
    @Exclude
    public void setUserId(String id) { setUserID(id); }

    // Legacy support for Admin (Address, catePrice)
    @Exclude
    public String getLegacyAddress() { return getAddress(); }
    @Exclude
    public void setLegacyAddress(String address) { setAddress(address); }
    @Exclude
    public String getCatePrice() { return getCutPrice(); }
    @Exclude
    public void setCatePrice(String price) { setCutPrice(price); }
}
