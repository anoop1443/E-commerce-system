package com.example.deliveryboy.withdrawal;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class WithdrawalRequest {
    private String documentId;
    private String deliveryBoyId;
    private double amount;
    private Timestamp timestamp;
    private String status;

    public WithdrawalRequest() {
        // Default constructor zaroori hai Firestore ke liye
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(String deliveryBoyId) {
        this.deliveryBoyId = deliveryBoyId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Helper method to format the Firestore Timestamp into a readable string.
     * @return Formatted date and time string or "N/A" if timestamp is null.
     */
    public String getFormattedTimestamp() {
        if (timestamp != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.getDefault());
            return sdf.format(timestamp.toDate());
        }
        return "N/A";
    }
}
