package com.example.recommendation.domain.search;

import java.util.StringJoiner;

import com.example.recommendation.domain.criteria.RecommendationCriteria;

/**
 * RecommendationCriteria -> 네이버 검색 query 생성기
 *
 * 원칙:
 * - 추론 ❌
 * - 중요도 판단 ❌
 * - 필터링 ❌
 *
 * 규칙:
 * - mainKeyword는 항상 포함
 * - optionKeywords는 모두 그대로 공백 결합
 * - 평가는 Evaluation 단계에서 다시 수행
 */
public final class NaverQueryMapper {

    private NaverQueryMapper() {
    }

    public static String toQuery(RecommendationCriteria criteria) {

        if (criteria.getSearchKeyword() == null || criteria.getSearchKeyword().isBlank()) {
            throw new IllegalArgumentException("searchKeyword is required");
        }

        StringJoiner joiner = new StringJoiner(" ");

        // 1️⃣ mainKeyword (필수)
        joiner.add(criteria.getSearchKeyword());

        // 2️⃣ optionKeywords (있으면 전부 검색에 반영)
        for (String option : criteria.getOptionKeywords()) {
            if (option != null && !option.isBlank()) {
                joiner.add(option);
            }
        }

        // ⚠️ preferredBrand는 아직 검색 조건으로 쓰지 않음
        // (원하면 이후 동일 패턴으로 추가 가능)

        return joiner.toString();
    }
}
