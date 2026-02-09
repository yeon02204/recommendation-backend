package com.example.recommendation.domain.criteria;

import java.util.List;

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
 * - 고려는 하지만 판단하지 않는다
 * - 모든 판단은 DecisionMaker의 책임이다
 */
public class RecommendationCriteria {

    private final String searchKeyword;
    private final List<String> optionKeywords;
    private final Integer priceMax;
    private final String preferredBrand;

    // 🔹 대화 상태 (State)
    private final UserIntentType intentType;

    // 🔹 명령 (Command) — 상태가 아님
    private final CommandType commandType;

    /**
     * ✅ 기존 생성자 (완전 유지)
     * - 기존 코드 / 테스트 / FakeClient 전부 호환
     * - intentType / commandType은 null 허용
     */
    public RecommendationCriteria(
            String searchKeyword,
            List<String> optionKeywords,
            Integer priceMax,
            String preferredBrand
    ) {
        this(
                searchKeyword,
                optionKeywords,
                priceMax,
                preferredBrand,
                null,
                null
        );
    }

    /**
     * ✅ Step 6 / 7 확장 생성자 (기존 용도 유지)
     * - Context 기반 intent만 있을 때
     */
    public RecommendationCriteria(
            String searchKeyword,
            List<String> optionKeywords,
            Integer priceMax,
            String preferredBrand,
            UserIntentType intentType
    ) {
        this(
                searchKeyword,
                optionKeywords,
                priceMax,
                preferredBrand,
                intentType,
                null
        );
    }

    /**
     * 🔥 최종 확장 생성자
     * - AI가 판단한 intent / command를 그대로 담는다
     * - 이 클래스는 해석하지 않는다
     */
    public RecommendationCriteria(
            String searchKeyword,
            List<String> optionKeywords,
            Integer priceMax,
            String preferredBrand,
            UserIntentType intentType,
            CommandType commandType
    ) {
        this.searchKeyword = searchKeyword;

        // 🔑 핵심 유지:
        // optionKeywords는 null이 아닌 "빈 리스트"로 보존
        // EvaluationService는 이 값을 그대로 신뢰한다
        this.optionKeywords =
                optionKeywords == null ? List.of() : List.copyOf(optionKeywords);

        this.priceMax = priceMax;
        this.preferredBrand = preferredBrand;
        this.intentType = intentType;
        this.commandType = commandType;
    }

    // ===== Getter =====

    public String getSearchKeyword() {
        return searchKeyword;
    }

    public List<String> getOptionKeywords() {
        return optionKeywords;
    }

    public Integer getPriceMax() {
        return priceMax;
    }

    public String getPreferredBrand() {
        return preferredBrand;
    }

    /**
     * 대화 상태 조회
     * - HOME / SEARCH / CONSULT
     * - 판단 ❌
     */
    public UserIntentType getIntentType() {
        return intentType;
    }

    /**
     * 명령 조회
     * - RESET / NONE
     * - 상태 아님
     */
    public CommandType getCommandType() {
        return commandType;
    }

    // ===== 🔽 EvaluationService 호환용 파생 메서드 =====

    /**
     * 브랜드 선호 여부
     * - 판단 아님
     * - preferredBrand 값 존재 여부만 노출
     */
    public boolean isBrandPreferred() {
        return preferredBrand != null && !preferredBrand.isBlank();
    }

    /**
     * 가격 조건 존재 여부
     * - EvaluationService 기존 로직 호환용
     */
    public String getPriceRange() {
        return priceMax != null ? "HAS_LIMIT" : null;
    }

    @Override
    public String toString() {
        return "RecommendationCriteria{" +
                "searchKeyword='" + searchKeyword + '\'' +
                ", optionKeywords=" + optionKeywords +
                ", priceMax=" + priceMax +
                ", preferredBrand='" + preferredBrand + '\'' +
                ", intentType=" + intentType +
                ", commandType=" + commandType +
                '}';
    }
}
