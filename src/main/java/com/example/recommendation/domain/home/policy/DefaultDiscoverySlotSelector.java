package com.example.recommendation.domain.home.policy;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;
import com.example.recommendation.domain.home.state.HomeConversationState;

// 현재 대화 상태를 보고 다음 질문 슬롯을 고르는 기본 구현체

@Service
public class DefaultDiscoverySlotSelector
        implements DiscoverySlotSelector {

    @Override
    public DecisionSlot select(HomeConversationState state) {

        for (DecisionSlot slot : DecisionSlot.values()) {
            SlotState slotState = state.getSlot(slot);

            if (slotState.getStatus() == SlotStatus.EMPTY) {
                return slot;
            }
        }

        // 전부 물어봤다면 fallback
        return DecisionSlot.CONTEXT;
    }
}
