package com.example.uiappfastfood.model;

public class OrderItem {
    private String name;
    private String price;

    private int count;
    private int imageResId;

    public OrderItem(String name, String price, int count, int imageResId) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImageResId() {
        return imageResId;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
