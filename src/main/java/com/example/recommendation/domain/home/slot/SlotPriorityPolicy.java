package com.example.recommendation.domain.home.slot;

import com.example.recommendation.domain.home.state.HomeConversationState;

// 슬롯 우선순위를 계산하는 규칙 인터페이스

public interface SlotPriorityPolicy {

    /**
     * 지금 상태에서
     * 다음에 질문할 슬롯 1개를 고른다
     */
    DecisionSlot nextSlot(HomeConversationState state);
}
