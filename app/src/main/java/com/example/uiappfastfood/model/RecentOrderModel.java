package com.example.uiappfastfood.model;

public class RecentOrderModel {
    private String name;
    private String category;
    private String imageUrl;

    public RecentOrderModel(String name, String category, String imageUrl) {
        this.name = name;
        this.category = category;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
}