package com.example.uiappfastfood.DTO.request;

public class RecentSearchRequest {
    private String keyword;
    private Long userId;

    public RecentSearchRequest(String keyword, Long userId) {
        this.keyword = keyword;
        this.userId = userId;
    }

    public RecentSearchRequest(Long userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public Long getUserId() {
        return userId;
    }
}
