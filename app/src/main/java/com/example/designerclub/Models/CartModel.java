package com.example.designerclub.Models;

public class CartModel {
    String Brand, Description, ImageUrl, ProductId, ProductName, Services, Size, Time, Date, DateAndTime, Quantity;
    String ProductPrice;

    public CartModel() {
    }

    public CartModel(String brand, String description, String imageUrl, String productId, String productName, String services, String size, String time, String date, String dateAndTime, String quantity, String productPrice) {
        Brand = brand;
        Description = description;
        ImageUrl = imageUrl;
        ProductId = productId;
        ProductName = productName;
        Services = services;
        Size = size;
        Time = time;
        Date = date;
        DateAndTime = dateAndTime;
        Quantity = quantity;
        ProductPrice = productPrice;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getServices() {
        return Services;
    }

    public void setServices(String services) {
        Services = services;
    }

    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDateAndTime() {
        return DateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        DateAndTime = dateAndTime;
    }

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
