package com.example.recommendation.domain.criteria;

/**
 * [역할]
 * - 추천 판단에 필요한 조건을 담는 순수 도메인 객체
 *
 * [설계 원칙]
 * - 이 객체는 "상태(State)"만 가진다
 * - 판단 로직 ❌
 * - confidence 개념 ❌
 * - followUpQuestion ❌
 * - 외부 서비스 호출 ❌
 * - AI 호출 ❌
 *
 * [이 객체가 할 수 있는 것]
 * - CriteriaService가 해석한 결과를 담는다
 * - Search / Evaluation / Decision 단계에서 읽히기만 한다
 *
 * [중요]
 * - 이 객체는 절대 "추천 가능/불가능"을 말하지 않는다
 * - 모든 판단은 DecisionMaker의 책임이다
 */
public class RecommendationCriteria {

    /**
     * 검색에 사용할 대표 키워드 (필수)
     * 예: "헤드셋", "무선 이어폰"
     */
    private String searchKeyword;

    /**
     * 가격 상한 (실제 검색/필터링용)
     * 예: 100000
     * 없으면 null
     */
    private Integer priceMax;

    /**
     * 가격 범위 (의도 표현용)
     * 실제 가격 판단은 Evaluation 단계에서 수행
     *
     * 예:
     * - UNDER_50K
     * - UNDER_100K
     * - UNDER_200K
     * - NO_LIMIT
     */
    private String priceRange;

    /**
     * 브랜드 선호 여부
     */
    private boolean brandPreferred;

    /* =====================
       Getter / Setter
       ===================== */

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public Integer getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    public String getPriceRange() {
        return priceRange;
    }

    public void setPriceRange(String priceRange) {
        this.priceRange = priceRange;
    }

    public boolean isBrandPreferred() {
        return brandPreferred;
    }

    public void setBrandPreferred(boolean brandPreferred) {
        this.brandPreferred = brandPreferred;
    }
}
