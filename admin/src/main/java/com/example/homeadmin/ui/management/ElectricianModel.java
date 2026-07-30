package com.example.homeadmin.ui.management;

import com.google.firebase.Timestamp;

public class ElectricianModel {
    private String uid;
    private String name;
    private String phone;
    private String email;
    private String profileImage;
    private String address;
    private String aadhaarNumber;
    private boolean isOnline;
    private double mainBalance;
    private long totalServicesCompleted;
    private String skills; // e.g., "MCB Installation, Wiring, AC Repair"
    private Timestamp joiningDate;

    public ElectricianModel() {
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }

    public boolean isOnline() { return isOnline; }
    public void setOnline(boolean online) { isOnline = online; }

    public double getMainBalance() { return mainBalance; }
    public void setMainBalance(double mainBalance) { this.mainBalance = mainBalance; }

    public long getTotalServicesCompleted() { return totalServicesCompleted; }
    public void setTotalServicesCompleted(long totalServicesCompleted) { this.totalServicesCompleted = totalServicesCompleted; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public Timestamp getJoiningDate() { return joiningDate; }
    public void setJoiningDate(Timestamp joiningDate) { this.joiningDate = joiningDate; }
}
