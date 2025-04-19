package com.example.uiappfastfood.DTO.response;

public class AutoComplete {
    private String description;

    public AutoComplete(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}