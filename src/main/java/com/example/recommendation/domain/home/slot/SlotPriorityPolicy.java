package com.example.recommendation.domain.home.slot;

import com.example.recommendation.domain.home.state.HomeConversationState;

public interface SlotPriorityPolicy {

    /**
     * 지금 상태에서
     * 다음에 질문할 슬롯 1개를 고른다
     */
    DecisionSlot nextSlot(HomeConversationState state);
}
