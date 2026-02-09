package com.example.recommendation.domain.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SearchReadinessEvaluator
 *
 * [역할]
 * - 현재 Context + Criteria를 보고
 *   "외부 검색을 해도 의미가 있는 상태인지"만 판단한다
 *
 * [절대 금지]
 * - 검색 실행 ❌
 * - 추천 판단 ❌
 * - Phase 결정 ❌
 *
 * → 오직 신호 해석
 */
@Component
public class SearchReadinessEvaluator {

    private static final Logger log =
            LoggerFactory.getLogger(SearchReadinessEvaluator.class);

    public SearchReadiness evaluate(
            ConversationContext context,
            RecommendationCriteria criteria
    ) {

        log.info("[SearchReadinessEvaluator] evaluate start");

        /* =========================
         * 🔥 1️⃣ AI intentType 판단
         * ========================= */
        if (criteria.getIntentType() == UserIntentType.HOME) {

            // 🔥 핵심 수정:
            // 이미 검색이 시작된 상태라면
            // HOME 발화는 "조건 추가"로 해석한다
            if (context.getConfirmedKeyword() != null) {
                log.info(
                    "[Evaluator] HOME intent but confirmedKeyword exists ('{}') → continue search",
                    context.getConfirmedKeyword()
                );
            } else {
                log.info("[Evaluator] AI가 HOME 판단 + keyword 없음 → 상담 필요");
                return SearchReadiness.NEED_MORE_CONTEXT;
            }
        }

        /* =========================
         * 2️⃣ searchKeyword 신호
         * ========================= */
        boolean hasMainKeyword =
                criteria.getSearchKeyword() != null &&
                !criteria.getSearchKeyword().isBlank();

        // criteria에는 없지만
        // context에 이미 확정 키워드가 있을 수 있음
        if (!hasMainKeyword && context.getConfirmedKeyword() == null) {
            log.info("[Evaluator] searchKeyword 없음 → 상담 필요");
            return SearchReadiness.NEED_MORE_CONTEXT;
        }

        /* =========================
         * 3️⃣ 추가 신호 체크
         * ========================= */
        boolean hasOption =
                criteria.getOptionKeywords() != null &&
                !criteria.getOptionKeywords().isEmpty();

        boolean hasBrand =
                criteria.getPreferredBrand() != null;

        boolean hasPrice =
                criteria.getPriceMax() != null;

        boolean hasConversationHistory =
                context.getTurnCount() >= 1;

        log.info(
            "[Evaluator] signals - keyword={}, option={}, brand={}, price={}, turnCount={}",
            hasMainKeyword || context.getConfirmedKeyword() != null,
            hasOption,
            hasBrand,
            hasPrice,
            context.getTurnCount()
        );

        /* =========================
         * 4️⃣ 추가 신호 부족 판단
         * ========================= */
        if (!hasOption && !hasBrand && !hasPrice && !hasConversationHistory) {
            log.info("[Evaluator] searchKeyword만 있고 추가 신호 부족 → 더 물어보기");
            return SearchReadiness.NEED_MORE_CONTEXT;
        }

        /* =========================
         * 5️⃣ 검색 준비 완료
         * ========================= */
        log.info("[Evaluator] READY_FOR_EVALUATION → 검색 가능");
        return SearchReadiness.READY_FOR_EVALUATION;
    }
}
