package com.example.recommendation.domain.criteria;

import java.util.HashSet;
import java.util.Set;

/**
 * [역할]
 * - 대화 흐름에서 누적되는 "상태"만 관리하는 객체
 *
 * [중요]
 * - 판단 ❌
 * - 분기 ❌
 * - 검색 실행 ❌
 *
 * → Orchestrator / Service 들이 읽고 활용만 한다
 */
public class ConversationContext {

    // 현재 대화 의도 (HOME / SEARCH만 누적)
    private UserIntentType intentType;

    // 확정된 메인 키워드
    private String confirmedKeyword;

    // 누적 옵션 키워드
    private final Set<String> optionKeywords = new HashSet<>();

    // 제외된 키워드
    private final Set<String> excludedKeywords = new HashSet<>();

    // 기타 조건
    private String preferredBrand;
    private Integer priceMax;

    // 턴 / 재검색 관리
    private int turnCount = 0;
    private int retryCount = 0;

    // =====================
    // 🔥 대화 진행 단계
    // =====================
    private ConversationPhase phase = ConversationPhase.DISCOVERY;

    // =====================
    // 🔥 추가: 마지막 검색 기준 (RETRY_SEARCH용)
    // =====================
    private RecommendationCriteria lastSearchCriteria;

    /* =====================
       Getter
       ===================== */

    public UserIntentType getIntentType() {
        return intentType;
    }

    public String getConfirmedKeyword() {
        return confirmedKeyword;
    }

    public Set<String> getOptionKeywords() {
        return optionKeywords;
    }

    public Set<String> getExcludedKeywords() {
        return excludedKeywords;
    }

    public String getPreferredBrand() {
        return preferredBrand;
    }

    public Integer getPriceMax() {
        return priceMax;
    }

    public int getTurnCount() {
        return turnCount;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public ConversationPhase getPhase() {
        return phase;
    }

    // 🔥 추가 Getter
    public RecommendationCriteria getLastSearchCriteria() {
        return lastSearchCriteria;
    }

    /* =====================
       Turn / Retry 관리
       ===================== */

    public void nextTurn() {
        this.turnCount++;
    }

    public void increaseRetryCount() {
        this.retryCount++;
    }

    public boolean shouldReset() {
        return turnCount >= 6;
    }

    /* =====================
       Criteria 병합
       ===================== */

    public void merge(RecommendationCriteria criteria) {

        // 🔥 intent 누적은 HOME / SEARCH만
        if (criteria.getIntentType() != null &&
            criteria.getIntentType() != UserIntentType.CONSULT) {
            this.intentType = criteria.getIntentType();
        }

        if (this.confirmedKeyword == null &&
            criteria.getSearchKeyword() != null) {
            this.confirmedKeyword = criteria.getSearchKeyword();
        }

        criteria.getOptionKeywords().forEach(option -> {
            if (!excludedKeywords.contains(option)) {
                optionKeywords.add(option);
            }
        });

        if (criteria.getPreferredBrand() != null) {
            this.preferredBrand = criteria.getPreferredBrand();
        }

        if (criteria.getPriceMax() != null) {
            this.priceMax = criteria.getPriceMax();
        }
    }

    /* =====================
       제외 처리
       ===================== */

    public void excludeKeyword(String keyword) {
        optionKeywords.remove(keyword);
        excludedKeywords.add(keyword);
    }

    /* =====================
       Context → Criteria 변환
       ===================== */

    public RecommendationCriteria toCriteria() {
        return new RecommendationCriteria(
                confirmedKeyword,
                optionKeywords.stream().toList(),
                priceMax,
                preferredBrand,
                intentType
        );
    }

    /* =====================
       🔥 Phase 업데이트
       ===================== */

    public void updatePhase(ConversationPhase nextPhase) {
        if (nextPhase == null) return;
        this.phase = nextPhase;
    }

    /* =====================
       🔥 마지막 검색 기준 저장
       ===================== */

    public void setLastSearchCriteria(RecommendationCriteria criteria) {
        if (criteria == null) return;
        this.lastSearchCriteria = criteria;
    }

    /* =====================
       초기화
       ===================== */

    public void reset() {
        intentType = null;
        confirmedKeyword = null;
        optionKeywords.clear();
        excludedKeywords.clear();
        preferredBrand = null;
        priceMax = null;
        turnCount = 0;
        retryCount = 0;
        phase = ConversationPhase.DISCOVERY;
        lastSearchCriteria = null; // 🔥 추가
    }
}
