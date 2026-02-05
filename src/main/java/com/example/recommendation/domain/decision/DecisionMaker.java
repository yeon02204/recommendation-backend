package com.example.recommendation.domain.decision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.explanation.ExplanationPolicy;

/**
 * [역할]
 * - EvaluationResult라는 "사실 데이터"를 해석하여
 *   추천 가능 여부를 판단한다.
 *
 * [원칙]
 * - 상품 선택 ❌
 * - 점수 계산 ❌
 * - AI 호출 ❌
 * - 문자열 조합 ❌
 * - 오직 상태 판단 + 정책 선택만 수행
 */
@Component
public class DecisionMaker {

    private static final Logger log =
            LoggerFactory.getLogger(DecisionMaker.class);

    public Decision decide(
            EvaluationResult result,
            RecommendationCriteria criteria
    ) {

        log.info("===== Decision Observation =====");
        log.info("candidateCount={}", result.getCandidateCount());
        log.info("topScore={}, secondScore={}",
                result.getTopScore(), result.getSecondScore());

        Decision decision;

        /* =========================
         * 1️⃣ 추천 불가: 후보 없음
         * ========================= */
        if (result.getCandidateCount() == 0) {
            decision = Decision.invalid(
                    "추천 가능한 상품이 없습니다."
            );
        }

        /* =========================
         * 2️⃣ 후보 1개: 즉시 추천 (예외)
         * ========================= */
        else if (result.getCandidateCount() == 1) {
            decision = Decision.recommend(
                    "조건에 맞는 최선의 상품을 추천합니다."
            );
        }

        /* =========================
         * 3️⃣ 애매함 (MVP 고정 정책)
         * ========================= */
        else if (
                result.getTopScore() - result.getSecondScore() <= 1
        ) {
            decision = Decision.requery(
                    "후보 상품 간 차이가 명확하지 않습니다.",
                    ExplanationPolicy.REQUERY_NEED_MORE_CONDITION
            );
        }

        /* =========================
         * 4️⃣ 명확한 추천
         * ========================= */
        else {
            decision = Decision.recommend(
                    "충분한 근거로 추천 가능합니다."
            );
        }

        /* =========================
         * 결과 로그
         * ========================= */
        log.info("===== Decision Result =====");
        log.info("type={}", decision.getType());
        log.info("confidence={}", decision.getConfidence());
        log.info("reason='{}'", decision.getReason());
        log.info("explanationPolicy={}", decision.getExplanationPolicy());

        return decision;
    }
}
