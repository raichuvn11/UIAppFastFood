package com.example.uiappfastfood.model;

import com.google.gson.annotations.SerializedName;

public class User {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private String address;
    private String img;

    public User(Long id, String username, String phone, String email, String address, String img) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.img = img;
    }

    public User(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
