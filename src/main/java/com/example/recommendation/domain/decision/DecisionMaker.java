package com.example.recommendation.domain.decision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.criteria.ConversationContext;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.home.HomeReason;

/**
 * [역할]
 * - EvaluationResult라는 "사실 데이터"를 해석하여
 *   추천 가능 상태만 판단한다.
 *
 * [원칙]
 * - 상품 선택 ❌
 * - 점수 계산 ❌
 * - AI 호출 ❌
 * - 설명 문자열 ❌
 *
 * → 오직 상태 전이 + 추천 가능 여부만 결정
 */
@Component
public class DecisionMaker {

    private static final Logger log =
            LoggerFactory.getLogger(DecisionMaker.class);

    public DecisionResult decide(
            ConversationContext context,
            RecommendationCriteria criteria,
            EvaluationResult result
    ) {

        log.info("===== DecisionMaker Observation =====");
        log.info("candidateCount={}", result.getCandidateCount());
        log.info("hasKeywordMatch={}, hasBrandMatch={}",
                result.hasKeywordMatch(),
                result.hasBrandMatch()
        );

        /* =========================
         * 1️⃣ 추천 불가: 후보 없음
         * ========================= */
        if (result.getCandidateCount() == 0) {
            log.info("[DecisionMaker] candidateCount=0 → DISCOVERY");
            return DecisionResult.discovery(
                    Decision.invalid(),
                    HomeReason.NEED_MORE_CONDITION
            );
        }

        /* =========================
         * 2️⃣ 후보 1개: 즉시 검색 가능
         * ========================= */
        if (result.getCandidateCount() == 1) {
            log.info("[DecisionMaker] candidateCount=1 → SEARCHING");
            return DecisionResult.searching(
                    Decision.recommend()
            );
        }

        /* =========================
         * 3️⃣ 일부 신호 존재: 추천 가능하지만 아직 탐색 유지
         * ========================= */
        if (result.hasKeywordMatch() || result.hasBrandMatch()) {
            log.info("[DecisionMaker] weak signal → READY");
            return DecisionResult.ready(
                    Decision.recommend()
            );
        }

        /* =========================
         * 4️⃣ 충분한 신호: 검색 가능
         * ========================= */
        log.info("[DecisionMaker] strong signal → SEARCHING");
        return DecisionResult.searching(
                Decision.recommend()
        );
    }
}
