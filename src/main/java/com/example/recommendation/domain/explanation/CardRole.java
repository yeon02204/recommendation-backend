package com.example.recommendation.domain.explanation;

/**
 * 카드 설명 톤 결정용 역할
 * - 점수 ❌
 * - 순위 ❌
 * - 사용자 노출 ❌
 */
public enum CardRole {
    OPTION_STRONG,   // 옵션 조건이 많이 맞음
    BRAND_TRUST,     // 브랜드 신뢰
    SINGLE_FOCUS,    // 핵심 조건 1개에 강함
    SAFE_CHOICE      // 무난한 선택
}
