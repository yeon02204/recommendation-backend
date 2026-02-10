package com.example.recommendation.domain.home.policy;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotPriority;
import com.example.recommendation.domain.home.state.HomeConversationState;

@Component
public class DefaultSlotSelectionPolicy
        implements SlotSelectionPolicy {

    @Override
    public DecisionSlot selectNext(HomeConversationState state) {

        for (DecisionSlot slot : SlotPriority.DEFAULT.getOrder()) {

            if (state.getSlot(slot).needsQuestion()) {
                return slot;
            }
        }

        // 더 물어볼 슬롯 없음
        return null;
    }
}
