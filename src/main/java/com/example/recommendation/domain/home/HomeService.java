package com.example.recommendation.domain.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.ConversationPhase;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.decision.DecisionType;
import com.example.recommendation.domain.explanation.ExplanationPolicy;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.dto.RecommendationResponseDto;

/**
 * HOME 단계 전용 서비스 (고도화)
 *
 * [역할]
 * - DecisionResult를 해석해
 *   "왜 아직 HOME인지"를 설명 문장으로 변환한다
 *
 * [원칙]
 * - 판단 ❌
 * - 검색 ❌
 * - 상태 전이 ❌
 *
 * → DecisionResult의 의미만 해석
 */

/**
 * [HOME 도메인 책임]
 *
 * - 검색 전 대화 공간
 * - 질문 생성
 * - 조건 정제
 *
 * [절대 금지]
 * - SearchService 호출 ❌
 * - 추천 결과 생성 ❌
 *
 * HOME은
 * "다음 대화 문장"만 만든다.
 */
@Service
public class HomeService {

    private static final Logger log =
            LoggerFactory.getLogger(HomeService.class);

    private final ExplanationService explanationService;

    public HomeService(ExplanationService explanationService) {
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

        log.info(
            "[HomeService] decisionType={}, phase={}",
            decisionType,
            phase
        );

        /* =========================
         * 1️⃣ 추천 불가
         * ========================= */
        if (decisionType == DecisionType.INVALID) {
            log.info("[HomeService] INVALID → 종료 메시지");
            return RecommendationResponseDto.invalid(
                    ExplanationPolicy.INVALID_NO_RESULT.getMessage()
            );
        }

        /* =========================
         * 2️⃣ DISCOVERY 단계
         * ========================= */
        if (phase == ConversationPhase.DISCOVERY) {

            if (criteria.getSearchKeyword() == null) {
                log.info(
                    "[HomeService] DISCOVERY + no keyword → requery mainKeyword"
                );
                return RecommendationResponseDto.requery(
                        ExplanationPolicy
                                .REQUERY_MAINKEYWORD_MISSING
                                .getMessage()
                );
            }

            log.info(
                "[HomeService] DISCOVERY + keyword exists → requery more condition"
            );
            return RecommendationResponseDto.requery(
                    ExplanationPolicy
                            .REQUERY_NEED_MORE_CONDITION
                            .getMessage()
            );
        }

        /* =========================
         * 3️⃣ READY 단계 (🔥 핵심 변경)
         * - 요약 / 방향 문장 책임자: ExplanationService
         * ========================= */
        if (phase == ConversationPhase.READY) {
            log.info("[HomeService] READY → ExplanationService delegation");

            String summary =
                    explanationService.generateReadySummary(
                            decisionResult,
                            criteria
                    );

            return RecommendationResponseDto.requery(summary);
        }

        /* =========================
         * 4️⃣ 안전망
         * ========================= */
        log.warn(
            "[HomeService] unexpected phase reached HOME (fallback)"
        );
        return RecommendationResponseDto.requery(
                ExplanationPolicy
                        .REQUERY_NEED_MORE_CONDITION
                        .getMessage()
        );
    }
}
