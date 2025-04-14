package com.example.uiappfastfood.model;

public class OrderStatus {
    private Long id;
    private String date;
    private String status;
    private Double total;

    public OrderStatus(Long id, String date, String status, Double total) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.total = total;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }
}
