package com.example.uiappfastfood.DTO.request;

public class AddToCartRequest {
    private Long userId;
    private Long menuItemId;

    public AddToCartRequest(Long userId, Long menuItemId) {
        this.userId = userId;
        this.menuItemId = menuItemId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getMenuItemId() {
        return menuItemId;
    }
}
