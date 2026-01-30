package com.example.recommendation.dto;

public class RecommendationResponseDto {

    private String message;

    public RecommendationResponseDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
