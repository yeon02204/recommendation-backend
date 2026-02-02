package com.example.recommendation.domain.decision;

public class Decision {

    private final DecisionType type;
    private final ConfidenceState confidence;

    // 판단 이유 (로그/설명용)
    private final String reason;

    // 재질문일 때만 사용 (사용자에게 노출되는 문장)
    private final String followUpQuestion;

    /* =========================
     * REQUERY 고정 템플릿 (문서 기준)
     * - AI 호출 없이 고정 문장만 사용
     * - 사유별로 1개 문장만 반환
     * - 여러 문장/질문 조합 금지
     * ========================= */

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

    private Decision(
            DecisionType type,
            ConfidenceState confidence,
            String reason,
            String followUpQuestion
    ) {
        this.type = type;
        this.confidence = confidence;
        this.reason = reason;
        this.followUpQuestion = followUpQuestion;
    }

    public static Decision invalid(String reason) {
        return new Decision(
                DecisionType.INVALID,
                ConfidenceState.INSUFFICIENT_DATA,
                reason,
                null
        );
    }

    /**
     * ✅ 문서 준수 강제:
     * - followUpQuestion은 "고정 템플릿" 중 하나만 허용한다.
     * - DecisionMaker가 어떤 문장을 넣어도, 사용자 노출 문장은 템플릿으로 강제된다.
     */
    public static Decision requery(String reason, String followUpQuestion) {
        String normalized = normalizeRequeryTemplate(followUpQuestion);

        return new Decision(
                DecisionType.REQUERY,
                ConfidenceState.WEAK_SIGNAL,
                reason,          // reason은 로그/내부용 (사용자 질문 텍스트가 아님)
                normalized       // 사용자에게 나갈 재질문 문장(고정 템플릿)
        );
    }

    public static Decision recommend(String reason) {
        return new Decision(
                DecisionType.RECOMMEND,
                ConfidenceState.STRONG_SIGNAL,
                reason,
                null
        );
    }

    /**
     * 입력 followUpQuestion이 고정 템플릿 중 하나인지 확인.
     * - 허용 템플릿이면 그대로 사용
     * - 아니면 기본 템플릿으로 강제
     *
     * 이유:
     * - REQUERY는 "AI 없이 고정 문장 1개" 규칙을 무조건 만족해야 함
     */
    private static String normalizeRequeryTemplate(String followUpQuestion) {
        if (followUpQuestion == null) {
            // null이면 기본 템플릿으로 강제
            return REQUERY_NEED_MORE_CONDITION;
        }

        // 고정 템플릿만 허용
        if (followUpQuestion.equals(REQUERY_MAINKEYWORD_MISSING)) return REQUERY_MAINKEYWORD_MISSING;
        if (followUpQuestion.equals(REQUERY_NEED_MORE_CONDITION)) return REQUERY_NEED_MORE_CONDITION;
        if (followUpQuestion.equals(REQUERY_PREFERRED_BRAND)) return REQUERY_PREFERRED_BRAND;
        if (followUpQuestion.equals(REQUERY_SEARCH_ZERO)) return REQUERY_SEARCH_ZERO;
        if (followUpQuestion.equals(REQUERY_FILTER_ZERO)) return REQUERY_FILTER_ZERO;

        // 어떤 문장이 들어와도 최종 사용자 노출 문장은 템플릿으로 강제
        return REQUERY_NEED_MORE_CONDITION;
    }

    public DecisionType getType() {
        return type;
    }

    public ConfidenceState getConfidence() {
        return confidence;
    }

    public String getReason() {
        return reason;
    }

    public String getFollowUpQuestion() {
        return followUpQuestion;
    }
}
