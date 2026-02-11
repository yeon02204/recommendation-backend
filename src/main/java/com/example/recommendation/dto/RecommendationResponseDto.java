package com.example.recommendation.dto;

import java.util.List;

import com.example.recommendation.domain.criteria.RecommendationCriteria;

public class RecommendationResponseDto {

    private final ResponseType type;
    private final String message;
    private final List<Item> items;
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

    /* ===================== */

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

    public static RecommendationResponseDto requery(String question) {
        return new RecommendationResponseDto(
                ResponseType.REQUERY,
                question,
                null,
                null
        );
    }

    public static RecommendationResponseDto invalid(String reason) {
        return new RecommendationResponseDto(
                ResponseType.INVALID,
                reason,
                null,
                null
        );
    }

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

    /* ===================== */
    /* 🔥 임시 SEARCH_READY */
    /* ===================== */

    public static RecommendationResponseDto searchReady(
            RecommendationCriteria criteria
    ) {
        // 프론트에 criteria를 그대로 전달하지 말고
        // message에 임시로 넘긴다 (오늘용)
        return new RecommendationResponseDto(
                ResponseType.SEARCH_READY,
                "SEARCH_READY",
                null,
                null
        );
    }

    /* ===================== */

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

        public Long getProductId() { return productId; }
        public String getTitle() { return title; }
        public String getImageUrl() { return imageUrl; }
        public String getLink() { return link; }
        public int getPrice() { return price; }
        public String getMallName() { return mallName; }
        public String getExplanation() { return explanation; }
    }

    public enum ResponseType {
        RECOMMEND,
        REQUERY,
        CONSULT,
        INVALID,
        SEARCH_READY
    }
}
