package com.example.recommendation.external.naver.dto;

/**
 * 네이버 쇼핑 API 요청 DTO
 *
 * 역할:
 * - HTTP 요청 파라미터를 명시적으로 표현
 *
 * 금지:
 * - 판단 ❌
 * - 키워드 조합 ❌
 * - 의미 해석 ❌
 */
public class NaverShopSearchRequest {

    private final String query;     // mainKeyword 그대로
    private final int display;      // 30 고정
    private final String sort;      // sim 고정
    private final Integer maxPrice; // priceMax (nullable)

    private NaverShopSearchRequest(
            String query,
            int display,
            String sort,
            Integer maxPrice
    ) {
        this.query = query;
        this.display = display;
        this.sort = sort;
        this.maxPrice = maxPrice;
    }

    public static NaverShopSearchRequest of(
            String query,
            Integer maxPrice
    ) {
        return new NaverShopSearchRequest(
                query,
                30,     // 정책 고정
                "sim",  // 정책 고정
                maxPrice
        );
    }

    public String getQuery() {
        return query;
    }

    public int getDisplay() {
        return display;
    }

    public String getSort() {
        return sort;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }
}
