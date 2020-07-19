package com.example.designerclub.Models;

public class CustomersModel {
    String userName,userEmail,userAddress,phone;

    public CustomersModel() {
    }

    public CustomersModel(String userName, String userEmail, String userAddress, String phone) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userAddress = userAddress;
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
