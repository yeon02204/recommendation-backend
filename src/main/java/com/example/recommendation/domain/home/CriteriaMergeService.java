package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * CONFIRMED 슬롯 → RecommendationCriteria 병합 서비스
 *
 * [역할]
 * - HomeConversationState의 CONFIRMED 슬롯들을 순회
 * - 각 슬롯의 값을 RecommendationCriteria에 병합
 *
 * [절대 금지]
 * - 판단 ❌
 * - AI 호출 ❌
 * - 검색 ❌
 */
@Service
public class CriteriaMergeService {

    /**
     * 기존 Criteria + HOME 슬롯 → 병합된 Criteria
     */
    public RecommendationCriteria merge(
            RecommendationCriteria base,
            HomeConversationState state
    ) {

        RecommendationCriteria result = base.copy();

        for (DecisionSlot slot : DecisionSlot.values()) {
            SlotState slotState = state.getSlot(slot);

            if (!slotState.isConfirmed()) {
                continue;
            }

            Object value = slotState.getValue();
            if (value == null) continue;

            switch (slot) {

                case TARGET -> result.setTarget(value.toString());

                case PURPOSE -> result.setPurpose(value.toString());

                case CONTEXT -> result.setContext(value.toString());

                case BUDGET -> {
                    if (value instanceof Integer budget) {
                        result.setPriceMax(budget);
                    }
                }

                case CONSTRAINT ->
                        result.addConstraint(value.toString());

                case PREFERENCE ->
                        result.addPreference(value.toString());
            }
        }

        return result;
    }
}