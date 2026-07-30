package com.example.homeelecation.ui.address;

public class AddressesSelectModel {

    private String fullName;
    private String mobileNumber;
    private String pinCode;
    private String state;
    private String city;
    private String house;
    private String roadAreaColony;
    private boolean selectAddresses;
    private String addressID;
    private String addressType;


    public AddressesSelectModel(String fullName, String mobileNumber, String pinCode, String state, String city, String house, String roadAreaColony, boolean selectAddresses, String addressID) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.pinCode = pinCode;
        this.state = state;
        this.city = city;
        this.house = house;
        this.roadAreaColony = roadAreaColony;
        this.selectAddresses = selectAddresses;
        this.addressID = addressID;
    }

    public AddressesSelectModel(String fullName, String mobileNumber, String pinCode, String state, String city, String house, String roadAreaColony, boolean selectAddresses, String addressID, String addressType) {
        this.fullName = fullName;
        this.mobileNumber = mobileNumber;
        this.pinCode = pinCode;
        this.state = state;
        this.city = city;
        this.house = house;
        this.roadAreaColony = roadAreaColony;
        this.selectAddresses = selectAddresses;
        this.addressID = addressID;
        this.addressType = addressType;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getRoadAreaColony() {
        return roadAreaColony;
    }

    public void setRoadAreaColony(String roadAreaColony) {
        this.roadAreaColony = roadAreaColony;
    }

    public boolean getSelectAddresses() {
        return selectAddresses;
    }

    public void setSelectAddresses(boolean selectAddresses) {
        this.selectAddresses = selectAddresses;
    }

    public String getAddressID(){
        return addressID;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
