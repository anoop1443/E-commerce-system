package com.example.homeadmin.ui.management;

public class StaffProfileModel {
    private String uid;
    private String name;
    private String mobile;
    private String role; // "Customer", "Delivery Boy", "Admin"
    private String status; // "Active", "Inactive", "Offline"
    private String profileImage;

    public StaffProfileModel() {
    }

    public StaffProfileModel(String uid, String name, String mobile, String role, String status, String profileImage) {
        this.uid = uid;
        this.name = name;
        this.mobile = mobile;
        this.role = role;
        this.status = status;
        this.profileImage = profileImage;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
}
