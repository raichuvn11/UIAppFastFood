package com.example.uiappfastfood.model;

public class DeviceTokenRequest {
    private Long userId;
    private String deviceToken;

    public DeviceTokenRequest(Long userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }

    public Long getUserId() { return userId; }
    public String getDeviceToken() { return deviceToken; }
}

