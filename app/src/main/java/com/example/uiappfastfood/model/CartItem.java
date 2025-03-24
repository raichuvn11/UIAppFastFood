package com.example.uiappfastfood.model;

public class CartItem {
    private String name;
    private String price;
    private int imageResId;

    private boolean isChecked;

    public CartItem(String name, String price, int imageResId, boolean isChecked) {
        this.name = name;
        this.price = price;
        this.imageResId = imageResId;
        this.isChecked = isChecked;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
