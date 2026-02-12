package com.example.recommendation.domain.criteria;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;
/**
 * [역할]
 * - 대화 맥락(Context)을 서버에서 관리
 *
 * [중요]
 * - 판단 ❌
 * - 상황 해석 ❌
 * - 리셋 결정 ❌
 *
 * → 오직 상태 누적 및 조회만 담당
 */
@Service
@SessionScope
public class ConversationContextService {

    // ⚠️ 현재는 단일 사용자 기준
    private ConversationContext context = new ConversationContext();

    /**
     * 새 Criteria를 Context에 병합
     * - 상태(State)만 누적
     * - commandType은 완전히 제거
     */
    public void merge(RecommendationCriteria newCriteria) {

        // 🔥 NEW: 상품 주제 변경 감지
        if (newCriteria.getSearchKeyword() != null
                && context.getConfirmedKeyword() != null
                && !newCriteria.getSearchKeyword()
                        .equals(context.getConfirmedKeyword())) {

            reset();
        }

        context.nextTurn();

        if (context.shouldReset()) {
            reset();
            return;
        }

        RecommendationCriteria criteriaForMerge =
                new RecommendationCriteria(
                        newCriteria.getSearchKeyword(),
                        newCriteria.getOptionKeywords(),
                        newCriteria.getPriceMax(),
                        newCriteria.getPreferredBrand(),
                        newCriteria.getIntentType(),
                        null
                );

        context.merge(criteriaForMerge);
    }


    /**
     * 병합 + 결과 반환 (편의 메서드)
     */
    public RecommendationCriteria mergeAndGet(RecommendationCriteria newCriteria) {
        System.out.println("🔥 현재 context 객체: " + context.hashCode());
        merge(newCriteria);
        return context.toCriteria();
    }


    /**
     * Context → Criteria 변환
     */
    public RecommendationCriteria toCriteria() {
        return context.toCriteria();
    }

    /**
     * Context 조회 (CONSULT 전용)
     */
    public ConversationContext getContext() {
        return context;
    }

    /**
     * 제외 키워드 처리
     */
    public void exclude(String keyword) {
        context.excludeKeyword(keyword);
    }

    /**
     * 명시적 리셋 (Orchestrator 전용)
     */
    public void reset() {
        System.out.println("🔥 RESET 호출됨");
        context = new ConversationContext();
    }

}
