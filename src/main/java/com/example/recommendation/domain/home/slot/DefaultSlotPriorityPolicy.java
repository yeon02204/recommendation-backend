package com.example.recommendation.domain.home.slot;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.state.HomeConversationState;

// 기본 슬롯 우선순위 규칙 구현체

@Component
public class DefaultSlotPriorityPolicy
        implements SlotPriorityPolicy {

    private static final DecisionSlot[] PRIORITY_ORDER = {
            DecisionSlot.TARGET,
            DecisionSlot.PURPOSE,
            DecisionSlot.CONSTRAINT,
            DecisionSlot.PREFERENCE,
            DecisionSlot.BUDGET,
            DecisionSlot.CONTEXT
    };

    @Override
    public DecisionSlot nextSlot(HomeConversationState state) {

        for (DecisionSlot slot : PRIORITY_ORDER) {
            SlotState slotState = state.getSlot(slot);

            if (slotState.getStatus() == SlotStatus.EMPTY) {
                return slot;
            }

            if (slotState.getStatus() == SlotStatus.USER_UNKNOWN) {
                return slot;
            }
        }

        return null; // 더 이상 물을 슬롯 없음 → READY 후보
    }
}
