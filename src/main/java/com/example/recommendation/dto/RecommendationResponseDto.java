package com.example.recommendation.dto;

import java.util.List;

/**
 * [역할]
 * - 추천 서버의 "외부 계약(Response Contract)"
 *
 * [중요]
 * - Domain 객체 노출 ❌
 * - Evaluation / Score 정보 노출 ❌
 * - 추천 서버가 보장하는 최소 결과만 포함
 *
 * [응답 케이스]
 * 1. RECOMMEND : 상품 ID + 설명
 * 2. REQUERY   : 추가 질문
 * 3. INVALID   : 추천 불가 사유
 */
public class RecommendationResponseDto {

    /**
     * 응답 타입
     */
    private final ResponseType type;

    /**
     * 설명 또는 질문 문장
     */
    private final String message;

    /**
     * 추천 상품 목록 (RECOMMEND일 때만 사용)
     */
    private final List<Item> items;

    private RecommendationResponseDto(
            ResponseType type,
            String message,
            List<Item> items
    ) {
        this.type = type;
        this.message = message;
        this.items = items;
    }

    /* =====================
       Factory methods
       ===================== */

    // 추천 성공
    public static RecommendationResponseDto recommend(
            List<Item> items,
            String explanation
    ) {
        return new RecommendationResponseDto(
                ResponseType.RECOMMEND,
                explanation,
                items
        );
    }

    // 재질문
    public static RecommendationResponseDto requery(
            String question
    ) {
        return new RecommendationResponseDto(
                ResponseType.REQUERY,
                question,
                List.of()
        );
    }

    // 추천 불가
    public static RecommendationResponseDto invalid(
            String reason
    ) {
        return new RecommendationResponseDto(
                ResponseType.INVALID,
                reason,
                List.of()
        );
    }

    /* =====================
       Getter
       ===================== */

    public ResponseType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public List<Item> getItems() {
        return items;
    }

    /* =====================
       Inner DTO
       ===================== */

    /**
     * 추천 서버가 외부로 보장하는 최소 상품 단위
     */
    public static class Item {

        private final Long productId;
        private final String explanation;

        public Item(Long productId, String explanation) {
            this.productId = productId;
            this.explanation = explanation;
        }

        public Long getProductId() {
            return productId;
        }

        public String getExplanation() {
            return explanation;
        }
    }

    /**
     * 응답 타입 enum (계약 고정)
     */
    public enum ResponseType {
        RECOMMEND,
        REQUERY,
        INVALID
    }
}
