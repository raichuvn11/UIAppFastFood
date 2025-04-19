package com.example.uiappfastfood.model;

public class Product {
    private String name;
    private String imageUrl;
    private String category;
    private double price;
    private boolean isFavorite;

    public Product(String name, String imageUrl, String category, double price, boolean isFavorite) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.category = category;
        this.price = price;
        this.isFavorite = isFavorite;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public boolean isFavorite() { return isFavorite; }

    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
