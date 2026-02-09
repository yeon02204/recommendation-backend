// =======================================================
// SearchReadiness.java
// =======================================================
package com.example.recommendation.domain.criteria;

/**
 * 검색 준비도 상태
 *
 * - 외부 검색 실행 여부 판단용
 * - 의미 판단 ❌
 * - 상태 표현 ⭕
 */
public enum SearchReadiness {
    NEED_MORE_CONTEXT,
    READY_FOR_EVALUATION
}
