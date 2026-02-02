package com.example.recommendation.domain.decision;

import com.example.recommendation.domain.explanation.ExplanationPolicy;

/**
 * [역할]
 * - 추천 판단 결과를 표현하는 도메인 객체
 *
 * [원칙]
 * - 문자열 직접 생성 ❌
 * - AI 호출 ❌
 * - Explanation 문장 선택 ❌
 *
 * Decision은
 * - DecisionType (RECOMMEND / REQUERY / INVALID)
 * - 판단 신뢰도
 * - 내부 판단 사유
 * - ExplanationPolicy
 * 만을 가진다.
 */
public class Decision {

    private final DecisionType type;
    private final ConfidenceState confidence;

    // 내부 판단 이유 (로그/디버깅용)
    private final String reason;

    // 사용자 노출 문구 정책 (실제 문장은 Explanation 단계에서 결정)
    private final ExplanationPolicy explanationPolicy;

    private Decision(
            DecisionType type,
            ConfidenceState confidence,
            String reason,
            ExplanationPolicy explanationPolicy
    ) {
        this.type = type;
        this.confidence = confidence;
        this.reason = reason;
        this.explanationPolicy = explanationPolicy;
    }

    /* =========================
     * Factory methods
     * ========================= */

    public static Decision invalid(String reason) {
        return new Decision(
                DecisionType.INVALID,
                ConfidenceState.INSUFFICIENT_DATA,
                reason,
                ExplanationPolicy.INVALID_NO_RESULT
        );
    }

    public static Decision requery(String reason, ExplanationPolicy policy) {
        return new Decision(
                DecisionType.REQUERY,
                ConfidenceState.WEAK_SIGNAL,
                reason,
                policy
        );
    }

    public static Decision recommend(String reason) {
        return new Decision(
                DecisionType.RECOMMEND,
                ConfidenceState.STRONG_SIGNAL,
                reason,
                ExplanationPolicy.RECOMMEND_CONFIDENT
        );
    }

    /* =========================
     * getters
     * ========================= */

    public DecisionType getType() {
        return type;
    }

    public ConfidenceState getConfidence() {
        return confidence;
    }

    public String getReason() {
        return reason;
    }

    public ExplanationPolicy getExplanationPolicy() {
        return explanationPolicy;
    }
}
