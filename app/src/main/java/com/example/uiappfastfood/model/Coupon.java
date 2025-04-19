package com.example.uiappfastfood.model;

import java.io.Serializable;

public class Coupon implements Serializable {
    private String code;
    private String type;
    private double discount;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
