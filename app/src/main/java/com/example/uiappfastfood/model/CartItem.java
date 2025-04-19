package com.example.uiappfastfood.model;

import com.google.gson.annotations.SerializedName;

public class CartItem {

    @SerializedName("itemId")
    private Long itemId;
    @SerializedName("name")
    private String name;
    @SerializedName("price")
    private Double price;
    @SerializedName("img")
    private String img;
    @SerializedName("quantity")
    private int quantity;

    private boolean isChecked;

    public CartItem(Long itemId, String name, Double price, int quantity, String img, boolean isChecked) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.img = img;
        this.isChecked = isChecked;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
