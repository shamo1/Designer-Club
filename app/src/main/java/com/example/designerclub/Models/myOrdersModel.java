package com.example.designerclub.Models;

public class myOrdersModel {
    String address, date, totalprice;

    public myOrdersModel() {
    }

    public myOrdersModel(String address, String date, String totalprice) {
        this.address = address;
        this.date = date;
        this.totalprice = totalprice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }
}
