package com.example.deliveryboy.earning;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class EarningRecord {
    private String orderID;
    private double amount;
    private Timestamp timestamp;
    private boolean isQuickOrder;
    private String status; // "Delivered" or "Cancelled"

    public EarningRecord() {}

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @PropertyName("isQuickOrder")
    public boolean isQuickOrder() { return isQuickOrder; }
    
    @PropertyName("isQuickOrder")
    public void setQuickOrder(boolean quickOrder) { isQuickOrder = quickOrder; }

    @PropertyName("orderID")
    public String getOrderID() { return orderID; }
    @PropertyName("orderID")
    public void setOrderID(String orderID) { this.orderID = orderID; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getFormattedDate() {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        return "N/A";
    }

    // Legacy support
    @Exclude
    public String getOrderId() { return getOrderID(); }
    @Exclude
    public void setOrderId(String id) { setOrderID(id); }
}
