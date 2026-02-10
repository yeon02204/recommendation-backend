package com.example.recommendation.domain.decision;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.recommendation.domain.criteria.ConversationPhase;
import com.example.recommendation.domain.home.HomeReason;

/**
 * [역할]
 * - DecisionMaker의 최종 판단 결과
 * - Orchestrator가 해석 없이 실행하기 위한 결과 묶음
 *
 * [원칙]
 * - 판단 ❌
 * - 검색 ❌
 * - 문자열 생성 ❌
 *
 * → 흐름 제어에 필요한 정보만 보유
 */
public class DecisionResult {

    private static final Logger log =
            LoggerFactory.getLogger(DecisionResult.class);

    private final Decision decision;
    private final ConversationPhase nextPhase;
    private final boolean allowSearch;
    private final HomeReason homeReason; // 🔥 HOME 전용 사유
    private final String reasoning;      // 로그/시스템 설명용

    private DecisionResult(
            Decision decision,
            ConversationPhase nextPhase,
            boolean allowSearch,
            HomeReason homeReason,
            String reasoning
    ) {
        this.decision = decision;
        this.nextPhase = nextPhase;
        this.allowSearch = allowSearch;
        this.homeReason = homeReason;
        this.reasoning = reasoning;

        log.info(
            "[DecisionResult] created decisionType={}, nextPhase={}, allowSearch={}, homeReason={}, reasoning={}",
            decision.getType(),
            nextPhase,
            allowSearch,
            homeReason,
            reasoning
        );
    }

    /* =========================
     * Factory methods
     * ========================= */

    /** 🔎 탐색 단계 유지 (HOME) - 사유 포함 */
    public static DecisionResult discovery(
            Decision decision,
            HomeReason homeReason
    ) {
        return new DecisionResult(
                decision,
                ConversationPhase.DISCOVERY,
                false,
                homeReason,
                "INSUFFICIENT_CONTEXT"
        );
    }

    /** 🔎 탐색 단계 유지 (HOME) - 사유 미지정 (기본값) */
    public static DecisionResult discovery(
            Decision decision
    ) {
        return discovery(decision, null);
    }

    /** ⏳ 추천 준비 완료 (아직 검색 안 함) */
    public static DecisionResult ready(Decision decision) {
        return new DecisionResult(
                decision,
                ConversationPhase.READY,
                false,
                null,
                "ENOUGH_CONTEXT_BUT_SEARCH_NOT_STARTED"
        );
    }

    /** 🔍 검색 수행 가능 단계 */
    public static DecisionResult searching(Decision decision) {
        return new DecisionResult(
                decision,
                ConversationPhase.SEARCHING,
                true,
                null,
                "READY_FOR_SEARCH_EXECUTION"
        );
    }

    /* =========================
     * getters
     * ========================= */

    public Decision getDecision() {
        return decision;
    }

    public ConversationPhase getNextPhase() {
        return nextPhase;
    }

    public boolean isAllowSearch() {
        return allowSearch;
    }

    public HomeReason getHomeReason() {
        return homeReason;
    }

    public String getReasoning() {
        return reasoning;
    }
}
