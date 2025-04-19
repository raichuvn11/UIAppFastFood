package com.example.uiappfastfood.model;


import java.util.List;

public class Category {
    private int id;
    private String type;
    private String imgCategory;
    private List<MenuItem> menuItems;

    public Category(int id, String type, String imgCategory, List<MenuItem> menuItems) {
        this.id = id;
        this.type = type;
        this.imgCategory = imgCategory;
        this.menuItems = menuItems;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgCategory() {
        return imgCategory;
    }

    public void setImgCategory(String imgCategory) {
        this.imgCategory = imgCategory;
    }

    public List<com.example.uiappfastfood.model.MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}

