package com.example.homeelecation.ui.address;

public class AddressesSelectModel {

    String  FullName;
    String FullAddress;
    String Phone;
    boolean SelectAddresses;

    public AddressesSelectModel(String fullName, String fullAddress, String phone, boolean selectAddresses) {
        FullName = fullName;
        FullAddress = fullAddress;
        Phone = phone;
        SelectAddresses = selectAddresses;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getFullAddress() {
        return FullAddress;
    }

    public void setFullAddress(String fullAddress) {
        FullAddress = fullAddress;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public boolean getSelectAddresses() {
        return SelectAddresses;
    }

    public void setSelectAddresses(boolean selectAddresses) {
        SelectAddresses = selectAddresses;
    }
}
