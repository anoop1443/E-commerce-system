package com.example.deliveryboy.withdrawal;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

public class TransactionRecord {
    private String transactionId;
    private double amount;
    private String status; // "Pending", "Approved", "Rejected", "Failed"
    private Timestamp requestDate;
    private Timestamp approvalDate;
    private Timestamp rejectDate;
    
    // Bank Details
    private String bankName;
    private String accountNumber;
    private String ifscCode;
    private String holderName;
    
    // Admin Info
    private String utrId;
    private String adminRemark;

    public TransactionRecord() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getRequestDate() { return requestDate; }
    public void setRequestDate(Timestamp requestDate) { this.requestDate = requestDate; }

    public Timestamp getApprovalDate() { return approvalDate; }
    public void setApprovalDate(Timestamp approvalDate) { this.approvalDate = approvalDate; }

    public Timestamp getRejectDate() { return rejectDate; }
    public void setRejectDate(Timestamp rejectDate) { this.rejectDate = rejectDate; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getIfscCode() { return ifscCode; }
    public void setIfscCode(String ifscCode) { this.ifscCode = ifscCode; }

    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }

    public String getUtrId() { return utrId; }
    public void setUtrId(String utrId) { this.utrId = utrId; }

    public String getAdminRemark() { return adminRemark; }
    public void setAdminRemark(String adminRemark) { this.adminRemark = adminRemark; }
}