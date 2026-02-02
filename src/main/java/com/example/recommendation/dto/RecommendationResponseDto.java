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
     * - REQUERY의 경우: "고정 템플릿 1개 문장"만 허용
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
       REQUERY 고정 템플릿 (외부 계약 준수)
       - AI 호출 없이 고정 문장만 사용
       - 사유별로 1개 문장만 반환
       - 여러 문장/질문 조합 금지
       ===================== */

    // 1) mainKeyword 없음 (price-only)
    public static final String REQUERY_MAINKEYWORD_MISSING =
            "어떤 종류의 상품을 찾고 계신가요?\n예: 헤드셋, 노트북, 선물용 아이템 등";

    // 2) 옵션/브랜드 근거 부족 또는 ambiguous
    public static final String REQUERY_NEED_MORE_CONDITION =
            "조금 더 구체적인 조건이 있다면 알려주세요.\n예: 사용 목적, 필요한 기능 등";

    // 3) 브랜드 미지정
    public static final String REQUERY_PREFERRED_BRAND =
            "선호하는 브랜드가 있다면 함께 알려주세요.";

    // 4) 검색 결과 0건
    public static final String REQUERY_SEARCH_ZERO =
            "조건에 맞는 상품을 찾지 못했습니다.\n다른 조건으로 다시 시도해볼까요?";

    // 5) Filter 후 0개
    public static final String REQUERY_FILTER_ZERO =
            "조건이 다소 까다로운 것 같습니다.\n일부 조건을 완화해보시겠어요?";

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
        // ✅ 외부 계약 강제:
        // - 어떤 문자열이 들어와도 최종 message는 "고정 템플릿" 중 하나만 허용
        String normalized = normalizeRequeryMessage(question);

        return new RecommendationResponseDto(
                ResponseType.REQUERY,
                normalized,
                null
        );
    }

    // 추천 불가
    public static RecommendationResponseDto invalid(
            String reason
    ) {
        return new RecommendationResponseDto(
                ResponseType.INVALID,
                reason,
                null
        );
    }

    /**
     * REQUERY 메시지를 고정 템플릿으로 정규화한다.
     * - 허용 템플릿이면 그대로 사용
     * - 아니면 기본 템플릿으로 강제
     *
     * 이유:
     * - REQUERY는 "고정 문장 1개" 규칙을 무조건 만족해야 함
     */
    private static String normalizeRequeryMessage(String question) {
        if (question == null) {
            return REQUERY_NEED_MORE_CONDITION;
        }

        if (question.equals(REQUERY_MAINKEYWORD_MISSING)) return REQUERY_MAINKEYWORD_MISSING;
        if (question.equals(REQUERY_NEED_MORE_CONDITION)) return REQUERY_NEED_MORE_CONDITION;
        if (question.equals(REQUERY_PREFERRED_BRAND)) return REQUERY_PREFERRED_BRAND;
        if (question.equals(REQUERY_SEARCH_ZERO)) return REQUERY_SEARCH_ZERO;
        if (question.equals(REQUERY_FILTER_ZERO)) return REQUERY_FILTER_ZERO;

        // 템플릿이 아니면 기본 템플릿으로 강제
        return REQUERY_NEED_MORE_CONDITION;
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
