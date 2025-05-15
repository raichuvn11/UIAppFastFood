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
}
