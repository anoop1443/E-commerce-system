package com.example.homeadmin.ui.address;

import com.google.firebase.firestore.PropertyName;

public class AddressesSelectModel {

    private String fullName;
    private String mobile;
    private String pinCode;
    private String state;
    private String city;
    private String house;
    private String area;
    private boolean selected;
    private String addressID;
    private String addressType;

    public AddressesSelectModel() {}

    public AddressesSelectModel(String fullName, String mobile, String pinCode, String state, String city, String house, String area, boolean selected, String addressID) {
        this.fullName = fullName;
        this.mobile = mobile;
        this.pinCode = pinCode;
        this.state = state;
        this.city = city;
        this.house = house;
        this.area = area;
        this.selected = selected;
        this.addressID = addressID;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPinCode() { return pinCode; }
    public void setPinCode(String pinCode) { this.pinCode = pinCode; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getHouse() { return house; }
    public void setHouse(String house) { this.house = house; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }

    @PropertyName("addressID")
    public String getAddressID() { return addressID; }
    @PropertyName("addressID")
    public void setAddressID(String addressID) { this.addressID = addressID; }

    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }

    // Legacy support for older field names
    @PropertyName("mobileNumber")
    public String getMobileNumber() { return getMobile(); }
    @PropertyName("mobileNumber")
    public void setMobileNumber(String mobileNumber) { setMobile(mobileNumber); }
    
    @PropertyName("roadAreaColony")
    public String getRoadAreaColony() { return getArea(); }
    @PropertyName("roadAreaColony")
    public void setRoadAreaColony(String roadAreaColony) { setArea(roadAreaColony); }
    
    @PropertyName("selectAddresses")
    public boolean getSelectAddresses() { return isSelected(); }
    @PropertyName("selectAddresses")
    public void setSelectAddresses(boolean selectAddresses) { setSelected(selectAddresses); }
}
