package com.example.recommendation.dto;

import java.util.List;

/**
 * 외부 응답 계약 DTO
 * - 프론트가 바로 렌더링 가능한 데이터만 포함
 */
public class RecommendationResponseDto {

    // 응답 타입 (계약 고정)
    private final ResponseType type;

    // 설명 문장 또는 재질문 문장
    private final String message;

    // 추천 상품 목록 (RECOMMEND 전용)
    private final List<Item> items;

    // 🔥 CONSULT 전용 (없을 수 있음)
    private final ConsultResponse consult;

    private RecommendationResponseDto(
            ResponseType type,
            String message,
            List<Item> items,
            ConsultResponse consult
    ) {
        this.type = type;
        this.message = message;
        this.items = items;
        this.consult = consult;
    }

    /* =====================
       REQUERY 고정 템플릿
       ===================== */

    public static final String REQUERY_MAINKEYWORD_MISSING =
            "어떤 종류의 상품을 찾고 계신가요?\n예: 헤드셋, 노트북, 선물용 아이템 등";

    public static final String REQUERY_NEED_MORE_CONDITION =
            "조금 더 구체적인 조건이 있다면 알려주세요.\n예: 사용 목적, 필요한 기능 등";

    public static final String REQUERY_PREFERRED_BRAND =
            "선호하는 브랜드가 있다면 함께 알려주세요.";

    public static final String REQUERY_SEARCH_ZERO =
            "조건에 맞는 상품을 찾지 못했습니다.\n다른 조건으로 다시 시도해볼까요?";

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
                items,
                null
        );
    }

    // 재질문
    public static RecommendationResponseDto requery(String question) {
        return new RecommendationResponseDto(
                ResponseType.REQUERY,
                normalizeRequeryMessage(question),
                null,
                null
        );
    }

    // 추천 불가
    public static RecommendationResponseDto invalid(String reason) {
        return new RecommendationResponseDto(
                ResponseType.INVALID,
                reason,
                null,
                null
        );
    }

    // 🔥 CONSULT 전용 응답
    public static RecommendationResponseDto consult(
            ConsultResponse consult
    ) {
        return new RecommendationResponseDto(
                ResponseType.CONSULT,
                consult.getMessage(),
                null,
                consult
        );
    }

    // REQUERY 문장 강제 정규화
    private static String normalizeRequeryMessage(String question) {
        if (question == null) return REQUERY_NEED_MORE_CONDITION;

        if (question.equals(REQUERY_MAINKEYWORD_MISSING)) return REQUERY_MAINKEYWORD_MISSING;
        if (question.equals(REQUERY_NEED_MORE_CONDITION)) return REQUERY_NEED_MORE_CONDITION;
        if (question.equals(REQUERY_PREFERRED_BRAND)) return REQUERY_PREFERRED_BRAND;
        if (question.equals(REQUERY_SEARCH_ZERO)) return REQUERY_SEARCH_ZERO;
        if (question.equals(REQUERY_FILTER_ZERO)) return REQUERY_FILTER_ZERO;

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

    public ConsultResponse getConsult() {
        return consult;
    }

    /* =====================
       Inner DTO (프론트용)
       ===================== */

    public static class Item {

        private final Long productId;
        private final String title;
        private final String imageUrl;
        private final String link;
        private final int price;
        private final String mallName;
        private final String explanation;

        public Item(
                Long productId,
                String title,
                String imageUrl,
                String link,
                int price,
                String mallName,
                String explanation
        ) {
            this.productId = productId;
            this.title = title;
            this.imageUrl = imageUrl;
            this.link = link;
            this.price = price;
            this.mallName = mallName;
            this.explanation = explanation;
        }

        public Long getProductId() {
            return productId;
        }

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getLink() {
            return link;
        }

        public int getPrice() {
            return price;
        }

        public String getMallName() {
            return mallName;
        }

        public String getExplanation() {
            return explanation;
        }
    }

    // 응답 타입 enum
    public enum ResponseType {
        RECOMMEND,
        REQUERY,
        CONSULT,   // 🔥 신규
        INVALID
    }
}
