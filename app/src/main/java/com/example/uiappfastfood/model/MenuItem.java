package com.example.uiappfastfood.model;

import java.util.List;

public class MenuItem {
    private int id;
    private String name;
    private String description;
    private double price;
    private int soldQuantity;
    private String createDate;
    private String imgMenuItem;
    private int categoryId;
    private List<Long> userFavoriteIds;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSoldQuantity() {
        return soldQuantity;
    }

    public void setSoldQuantity(int soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getImgMenuItem() {
        return imgMenuItem;
    }

    public void setImgMenuItem(String imgMenuItem) {
        this.imgMenuItem = imgMenuItem;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public List<Long> getUserFavoriteIds() {
        return userFavoriteIds;
    }

    public void setUserFavoriteIds(List<Long> userFavoriteIds) {
        this.userFavoriteIds = userFavoriteIds;
    }

    public int getFavoriteCount() {
        return userFavoriteIds != null ? userFavoriteIds.size() : 0;
    }
}
