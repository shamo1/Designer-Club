package com.example.designerclub.Models;

public class ProductsModel {
    String Brand, Description, ImageUrl, ProductId, ProductName, ProductPrice, Services;

    public ProductsModel() {
    }
    public ProductsModel(String brand, String description, String imageUrl, String productId, String productName, String productPrice, String services) {
        Brand = brand;
        Description = description;
        ImageUrl = imageUrl;
        ProductId = productId;
        ProductName = productName;
        ProductPrice = productPrice;
        Services = services;
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

    public String getProductPrice() {
        return ProductPrice;
    }

    public void setProductPrice(String productPrice) {
        ProductPrice = productPrice;
    }

    public String getServices() {
        return Services;
    }

    public void setServices(String services) {
        Services = services;
    }
}
