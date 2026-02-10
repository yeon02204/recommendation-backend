package com.example.recommendation.domain.criteria;

import com.example.recommendation.domain.home.HomeReason;

/**
 * SearchReadiness 평가 결과
 *
 * - readiness: 검색 가능 여부
 * - reason: 검색 불가 시 HOME 단계에서 사용할 사유
 */
public record SearchReadinessResult(
        SearchReadiness readiness,
        HomeReason reason
) {

    public static SearchReadinessResult ready() {
        return new SearchReadinessResult(
                SearchReadiness.READY_FOR_EVALUATION,
                null
        );
    }

    public static SearchReadinessResult needMore(HomeReason reason) {
        return new SearchReadinessResult(
                SearchReadiness.NEED_MORE_CONTEXT,
                reason
        );
    }
}
