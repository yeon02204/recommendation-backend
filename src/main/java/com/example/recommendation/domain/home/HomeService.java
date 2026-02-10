package com.example.recommendation.domain.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.ConversationPhase;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.decision.DecisionType;
import com.example.recommendation.dto.RecommendationResponseDto;

/**
 * HOME 단계 전용 서비스
 *
 * [역할]
 * - DecisionResult를 해석하여
 *   HOME 단계에서 사용할 HomeReason을 읽어낸다
 *
 * [절대 금지]
 * - 판단 ❌
 * - 검색 ❌
 * - 문장 생성 ❌
 */
@Service
public class HomeService {

    private static final Logger log =
            LoggerFactory.getLogger(HomeService.class);

    private final HomeExplanationService explanationService;

    public HomeService(HomeExplanationService explanationService) {
        this.explanationService = explanationService;
    }

    public RecommendationResponseDto handle(
            DecisionResult decisionResult,
            RecommendationCriteria criteria
    ) {

        DecisionType decisionType =
                decisionResult.getDecision().getType();
        ConversationPhase phase =
                decisionResult.getNextPhase();
        HomeReason reason =
                decisionResult.getHomeReason();

        log.info(
            "[HomeService] decisionType={}, phase={}, reason={}",
            decisionType,
            phase,
            reason
        );

        /* =========================
         * 1️⃣ 추천 불가
         * ========================= */
        if (decisionType == DecisionType.INVALID) {
            return RecommendationResponseDto.invalid(
                    "추천 가능한 상품이 없습니다."
            );
        }

        /* =========================
         * 2️⃣ DISCOVERY 단계
         * ========================= */
        if (phase == ConversationPhase.DISCOVERY) {

            HomeReason resolved =
                    reason != null
                            ? reason
                            : HomeReason.NEED_MORE_CONDITION;

            String message =
                    explanationService.generateRequery(
                            resolved,
                            criteria
                    );

            return RecommendationResponseDto.requery(message);
        }

        /* =========================
         * 3️⃣ READY 단계 (검색 직전 요약)
         * ========================= */
        if (phase == ConversationPhase.READY) {

            String summary =
                    explanationService.generateReadySummary(
                            criteria
                    );

            return RecommendationResponseDto.requery(summary);
        }

        /* =========================
         * 4️⃣ 안전망
         * ========================= */
        String fallback =
                explanationService.generateRequery(
                        HomeReason.NEED_MORE_CONDITION,
                        criteria
                );

        return RecommendationResponseDto.requery(fallback);
    }
}
