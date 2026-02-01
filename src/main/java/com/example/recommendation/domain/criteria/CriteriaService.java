package com.example.recommendation.domain.criteria;

import org.springframework.stereotype.Service;

/**
 * [역할]
 * - 사용자 자연어 입력을 분석하여 RecommendationCriteria를 생성한다.
 *
 * [중요 원칙]
 * - 이 클래스는 "기준 생성"만 책임진다.
 * - 추천 가능 / 불가능 판단 ❌
 * - REQUERY / INVALID 결정 ❌
 * - Decision 로직 ❌
 *
 * [허용]
 * - 문자열 패턴 기반 규칙
 * - 키워드 존재 여부 판단
 *
 * [금지]
 * - OpenAI 호출 ❌
 * - 외부 API 호출 ❌
 * - 점수 계산 ❌
 * - confidence / followUpQuestion 설정 ❌
 */
@Service
public class CriteriaService {

    /**
     * 사용자 입력을 기반으로 추천 기준을 생성한다.
     *
     * @param userInput 사용자 자연어 입력
     * @return RecommendationCriteria (상태만 포함)
     */
    public RecommendationCriteria createCriteria(String userInput) {

        RecommendationCriteria criteria = new RecommendationCriteria();

        // 입력 방어 (판단 아님, 안전 처리)
        if (userInput == null || userInput.isBlank()) {
            return criteria;
        }

        // =========================
        // 1️⃣ 검색 키워드 생성 (필수)
        // =========================
        // MVP 기준: 원문을 그대로 대표 키워드로 사용
        // (추후 mainKeyword 추출 로직으로 교체 가능)
        criteria.setSearchKeyword(userInput.trim());

        // =========================
        // 2️⃣ 가격 관련 키워드 (의도 표현)
        // =========================
        // 더 구체적인 표현이 우선되며, 나중 매칭이 덮어씀

        if (userInput.contains("저렴")) {
            criteria.setPriceRange("UNDER_50K");
            criteria.setPriceMax(50_000);
        }

        if (userInput.contains("가성비")) {
            criteria.setPriceRange("UNDER_100K");
            criteria.setPriceMax(100_000);
        }

        // =========================
        // 3️⃣ 브랜드 선호 여부
        // =========================
        if (userInput.contains("브랜드")) {
            criteria.setBrandPreferred(true);
        }

        // ⚠️ 중요
        // 여기서는:
        // - 추천 가능 여부 판단 ❌
        // - confidence 설정 ❌
        // - followUpQuestion 설정 ❌
        //
        // 이 객체는 "사실 데이터"만 담는다.

        return criteria;
    }
}
