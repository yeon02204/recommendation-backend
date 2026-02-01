package com.example.recommendation.domain.decision;

public class Decision {

    private final DecisionType type;
    private final ConfidenceState confidence;

    // 판단 이유 (로그/설명용)
    private final String reason;

    // 재질문일 때만 사용
    private final String followUpQuestion;

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

    public static Decision requery(String reason, String followUpQuestion) {
        return new Decision(
                DecisionType.REQUERY,
                ConfidenceState.WEAK_SIGNAL,
                reason,
                followUpQuestion
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
