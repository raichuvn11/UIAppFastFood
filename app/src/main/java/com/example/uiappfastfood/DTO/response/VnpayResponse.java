package com.example.uiappfastfood.DTO.response;

public class VnpayResponse {
    private String code;
    private String data;
    private String message;

    // Constructors
    public VnpayResponse() {}

    public VnpayResponse(String code, String data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
