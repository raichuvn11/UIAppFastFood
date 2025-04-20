package com.example.uiappfastfood.DTO.request;

public class VerifyOtpRequest {

    private String username;
    private String password;

    private String email;
    private String otp;

    public VerifyOtpRequest(String username,String email, String password,  String otp) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.otp = otp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getters and setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
