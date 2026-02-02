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
 * - Controller 입력을 도메인 흐름으로 연결
 * - 하위 단계 예외를 흡수하여 응답 형태를 보장
 *
 * [중요]
 * - 이 클래스는 "요청 하나의 생명주기"를 책임진다
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

    /**
     * 추천 요청 진입점
     * - 이 메서드는 반드시 RecommendationResponseDto를 반환해야 한다
     */
    public RecommendationResponseDto handle(RecommendationRequestDto request) {

        try {
            // 1️⃣ 요청 유효성 최소 방어
            if (request == null || request.getUserInput() == null) {
                return RecommendationResponseDto.invalid(
                        "요청이 올바르지 않습니다."
                );
            }

            // 2️⃣ 사용자 입력 → Criteria 생성
            RecommendationCriteria criteria =
                    criteriaService.createCriteria(
                            request.getUserInput()
                    );

            // 3️⃣ 추천 흐름 위임 (Search → Evaluation → Decision)
            return recommendationService.recommend(criteria);

        } catch (Exception e) {
            // 4️⃣ 하위 로직 예외 흡수 (서버 보호)
            // 로그는 추후 추가, 지금은 응답 안정성 우선
            return RecommendationResponseDto.invalid(
                    "추천 처리 중 오류가 발생했습니다."
            );
        }
    }
}
