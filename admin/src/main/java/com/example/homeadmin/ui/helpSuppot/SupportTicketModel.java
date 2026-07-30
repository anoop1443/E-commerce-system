package com.example.homeadmin.ui.helpSuppot;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.PropertyName;
import java.util.Date;

public class SupportTicketModel {

    private String ticketID;
    private String orderID;
    private String userID;
    private String issueDescription;
    private String status;
    private Date createdDate;
    private Date lastUpdateDate;
    private String adminNote;
    private String type;
    
    public SupportTicketModel() {
    }

    public SupportTicketModel(String ticketID, String orderID, String userID, String issueDescription, String status, Date createdDate) {
        this.ticketID = ticketID;
        this.orderID = orderID;
        this.userID = userID;
        this.issueDescription = issueDescription;
        this.status = status;
        this.createdDate = createdDate;
    }

    @PropertyName("ticketID")
    public String getTicketID() { return ticketID; }
    @PropertyName("ticketID")
    public void setTicketID(String ticketID) { this.ticketID = ticketID; }

    @PropertyName("orderID")
    public String getOrderID() { return orderID; }
    @PropertyName("orderID")
    public void setOrderID(String orderID) { this.orderID = orderID; }

    @PropertyName("userID")
    public String getUserID() { return userID; }
    @PropertyName("userID")
    public void setUserID(String userID) { this.userID = userID; }

    public String getIssueDescription() { return issueDescription; }
    public void setIssueDescription(String issueDescription) { this.issueDescription = issueDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }

    public Date getLastUpdateDate() { return lastUpdateDate; }
    public void setLastUpdateDate(Date lastUpdateDate) { this.lastUpdateDate = lastUpdateDate; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Legacy support
    @Exclude
    public String getTicketId() { return getTicketID(); }
    @Exclude
    public void setTicketId(String id) { setTicketID(id); }
    @Exclude
    public String getOrderId() { return getOrderID(); }
    @Exclude
    public void setOrderId(String id) { setOrderID(id); }
    @Exclude
    public String getUserId() { return getUserID(); }
    @Exclude
    public void setUserId(String id) { setUserID(id); }
    @Exclude
    public String getIssue_description() { return getIssueDescription(); }
    @Exclude
    public void setIssue_description(String desc) { setIssueDescription(desc); }
    @Exclude
    public Date getTimestamp() { return getCreatedDate(); }
    @Exclude
    public void setTimestamp(Date date) { setCreatedDate(date); }
    @Exclude
    public String getResolution_note() { return getAdminNote(); }
    @Exclude
    public void setResolution_note(String note) { setAdminNote(note); }
}
