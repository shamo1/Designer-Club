package com.example.designerclub.Models;

public class ProductReviewModel {
    String Name,Comment;
    Float Rating;

    public ProductReviewModel() {
    }

    public ProductReviewModel(String name, String comment, Float rating) {
        Name = name;
        Comment = comment;
        Rating = rating;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public Float getRating() {
        return Rating;
    }

    public void setRating(Float rating) {
        Rating = rating;
    }
}
