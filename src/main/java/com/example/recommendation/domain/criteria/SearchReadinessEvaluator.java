package com.example.recommendation.domain.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.HomeReason;

/**
 * SearchReadinessEvaluator
 *
 * [역할]
 * - 현재 Context + Criteria를 보고
 *   "지금 검색을 해도 되는지"를 판단한다
 * - 검색이 불가능한 경우,
 *   HOME 단계에서 사용할 사유(HomeReason)를 태깅한다
 *
 * [절대 금지]
 * - 검색 실행 ❌
 * - 추천 판단 ❌
 * - 문장 생성 ❌
 * - Phase 전이 직접 결정 ❌
 *
 * → 신호 해석 + 사유 태깅 전용
 */
@Component
public class SearchReadinessEvaluator {

    private static final Logger log =
            LoggerFactory.getLogger(SearchReadinessEvaluator.class);

    public SearchReadinessResult evaluate(
            ConversationContext context,
            RecommendationCriteria criteria
    ) {

        log.info("[SearchReadinessEvaluator] evaluate start");

        /* =========================
         * 1️⃣ HOME intent 처리
         * ========================= */
        if (criteria.getIntentType() == UserIntentType.HOME) {

            if (context.getConfirmedKeyword() == null) {
                log.info("[Evaluator] HOME + no keyword → NO_KEYWORD");
                return SearchReadinessResult.needMore(
                        HomeReason.NO_KEYWORD
                );
            }

            log.info("[Evaluator] HOME but keyword exists → continue");
        }

        /* =========================
         * 2️⃣ keyword 존재 여부
         * ========================= */
        boolean hasMainKeyword =
                criteria.getSearchKeyword() != null &&
                !criteria.getSearchKeyword().isBlank();

        if (!hasMainKeyword && context.getConfirmedKeyword() == null) {
            log.info("[Evaluator] no keyword anywhere → NO_KEYWORD");
            return SearchReadinessResult.needMore(
                    HomeReason.NO_KEYWORD
            );
        }

        /* =========================
         * 3️⃣ 추가 조건 신호
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
            "[Evaluator] signals - option={}, brand={}, price={}, turnCount={}",
            hasOption,
            hasBrand,
            hasPrice,
            context.getTurnCount()
        );

        /* =========================
         * 4️⃣ 조건 부족
         * ========================= */
        if (!hasOption && !hasBrand && !hasPrice && !hasConversationHistory) {
            log.info("[Evaluator] keyword only → NEED_MORE_CONDITION");
            return SearchReadinessResult.needMore(
                    HomeReason.NEED_MORE_CONDITION
            );
        }

        /* =========================
         * 5️⃣ 검색 가능
         * ========================= */
        log.info("[Evaluator] READY_FOR_EVALUATION");
        return SearchReadinessResult.ready();
    }
}
