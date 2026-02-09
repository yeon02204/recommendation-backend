package com.example.recommendation.domain.decision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.criteria.ConversationContext;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;

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
/**
 * ⚠️ [CRITICAL - USAGE CONTRACT]
 *
 * DecisionMaker는 "검색 전(pre-search) 판단자"다.
 *
 * 이 클래스는:
 * - 검색 실행 이전에만 호출되어야 한다.
 * - "지금 검색을 시작해도 되는가?"
 * - "추가 질문이 필요한가?"
 * 를 판단하는 용도다.
 *
 * ❌ 검색(SearchService) 실행 이후 호출 금지
 * ❌ 검색 결과(Product 리스트)가 존재하는 상태에서 호출 금지
 *
 * 이 규칙을 어기면
 * - READY / SEARCH_NOT_STARTED 같은 상태 의미가 붕괴한다.
 *
 * [이유]
 * DecisionMaker는 "검색 완료"라는 상태를 알지 못하도록 설계되었다.
 * 검색 이후 판단은 RecommendationService / HomeService의 책임이다.
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
                    Decision.invalid()
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
