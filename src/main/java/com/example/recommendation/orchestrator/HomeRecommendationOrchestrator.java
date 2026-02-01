package com.example.recommendation.orchestrator;

import com.example.recommendation.domain.criteria.CriteriaService;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.recommendation.RecommendationService;
import com.example.recommendation.dto.RecommendationRequestDto;
import com.example.recommendation.dto.RecommendationResponseDto;
import org.springframework.stereotype.Component;

/**
 * [역할]
 * - 추천 요청의 전체 흐름을 조율하는 조정자
 *
 * [책임]
 * - Controller에서 받은 요청을 Criteria로 변환
 * - RecommendationService로 위임
 *
 * [금지]
 * - 추천 판단 ❌
 * - 조건 해석 ❌
 * - 로직 처리 ❌
 */
@Component
public class HomeRecommendationOrchestrator {

    private final RecommendationService recommendationService;
    private final CriteriaService criteriaService;

    public HomeRecommendationOrchestrator(
            RecommendationService recommendationService,
            CriteriaService criteriaService
    ) {
        this.recommendationService = recommendationService;
        this.criteriaService = criteriaService;
    }

    public RecommendationResponseDto handle(RecommendationRequestDto request) {

        // 1️⃣ 사용자 입력 → Criteria 생성
        RecommendationCriteria criteria =
                criteriaService.createCriteria(
                        request.getUserInput()
                );

        // 2️⃣ 추천 흐름 위임
        return recommendationService.recommend(criteria);
    }
}
