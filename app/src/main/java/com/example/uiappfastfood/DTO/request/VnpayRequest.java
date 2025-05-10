package com.example.uiappfastfood.DTO.request;

public class VnpayRequest {
    private int amount;
    private String bankCode;
    private String language;
    private String ipAddress;
    private Long orderId;

    public VnpayRequest() {}
    public VnpayRequest(int amount, String bankCode, String language, String ipAddress, Long orderId) {
        this.amount = amount;
        this.bankCode = bankCode;
        this.language = language;
        this.ipAddress = ipAddress;
        this.orderId = orderId;

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
