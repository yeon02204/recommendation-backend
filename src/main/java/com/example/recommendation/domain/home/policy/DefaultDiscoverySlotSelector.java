package com.example.recommendation.domain.home.policy;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;
import com.example.recommendation.domain.home.state.HomeConversationState;

@Component
public class DefaultDiscoverySlotSelector
        implements DiscoverySlotSelector {

    @Override
    public DecisionSlot selectNext(HomeConversationState state) {

        // 1️⃣ 아직 안 물어본 슬롯
        for (SlotState slot : state.getAll().values()) {
            if (slot.getStatus() == SlotStatus.EMPTY) {
                return slot.getSlot();
            }
        }

        // 2️⃣ 사용자가 모른다고 한 슬롯
        for (SlotState slot : state.getAll().values()) {
            if (slot.getStatus() == SlotStatus.USER_UNKNOWN) {
                return slot.getSlot();
            }
        }

        // 3️⃣ 질문은 했지만 확정 안 된 슬롯
        for (SlotState slot : state.getAll().values()) {
            if (slot.getStatus() == SlotStatus.ASKED) {
                return slot.getSlot();
            }
        }

        // 4️⃣ 전부 확정됨 → READY
        return null;
    }
}
