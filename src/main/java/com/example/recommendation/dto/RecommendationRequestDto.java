package com.example.recommendation.dto;

/**
 * [역할]
 * - 추천 요청을 표현하는 DTO
 * - Controller → Service 전달용
 *
 * [중요]
 * - 비즈니스 로직 없음
 * - 검증/판단 없음
 * - 순수 데이터 홀더
 */
public class RecommendationRequestDto {

    // 사용자의 자연어 입력
    private String userInput;

    public RecommendationRequestDto() {
    }

    public RecommendationRequestDto(String userInput) {
        this.userInput = userInput;
    }

    public String getUserInput() {
        return userInput;
    }

    public void setUserInput(String userInput) {
        this.userInput = userInput;
    }
}
