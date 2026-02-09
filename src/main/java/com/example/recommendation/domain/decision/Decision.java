package com.example.recommendation.domain.decision;

import com.example.recommendation.domain.explanation.ExplanationPolicy;

/**
 * [역할]
 * - 추천 판단 결과를 표현하는 도메인 객체
 *
 * [원칙]
 * - 문자열 직접 생성 ❌
 * - AI 호출 ❌
 * - 설명 문장 보유 ❌
 *
 * Decision은
 * - DecisionType
 * - ConfidenceState
 * - ExplanationPolicy
 * 만을 가진다.
 */
public class Decision {

    private final DecisionType type;
    private final ConfidenceState confidence;
    private final ExplanationPolicy explanationPolicy;

    private Decision(
            DecisionType type,
            ConfidenceState confidence,
            ExplanationPolicy explanationPolicy
    ) {
        this.type = type;
        this.confidence = confidence;
        this.explanationPolicy = explanationPolicy;
    }

    /* =========================
     * Factory methods
     * ========================= */

    public static Decision invalid() {
        return new Decision(
                DecisionType.INVALID,
                ConfidenceState.INSUFFICIENT_DATA,
                ExplanationPolicy.INVALID_NO_RESULT
        );
    }

    /** ✅ 기존 메서드 (유지) */
    public static Decision requery(ExplanationPolicy policy) {
        return new Decision(
                DecisionType.REQUERY,
                ConfidenceState.WEAK_SIGNAL,
                policy
        );
    }

    /** ✅ 고도화 단계용 기본 requery (추가) */
    public static Decision requery() {
        return new Decision(
                DecisionType.REQUERY,
                ConfidenceState.WEAK_SIGNAL,
                ExplanationPolicy.REQUERY_NEED_MORE_CONDITION
        );
    }

    public static Decision recommend() {
        return new Decision(
                DecisionType.RECOMMEND,
                ConfidenceState.STRONG_SIGNAL,
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

    public ExplanationPolicy getExplanationPolicy() {
        return explanationPolicy;
    }
}
