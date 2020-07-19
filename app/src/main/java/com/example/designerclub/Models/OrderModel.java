package com.example.designerclub.Models;

public class OrderModel {
    String date, time, nodePhone, phone, address, name, totalprice, Uid;

    public OrderModel() {
    }

    public OrderModel(String date, String time, String nodePhone, String phone, String address, String name, String totalprice, String uid) {
        this.date = date;
        this.time = time;
        this.nodePhone = nodePhone;
        this.phone = phone;
        this.address = address;
        this.name = name;
        this.totalprice = totalprice;
        this.Uid = uid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNodePhone() {
        return nodePhone;
    }

    public void setNodePhone(String nodePhone) {
        this.nodePhone = nodePhone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(String totalprice) {
        this.totalprice = totalprice;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
