package com.example.recommendation.domain.home.policy;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

// DISCOVERY 단계에서 “다음에 물어볼 슬롯”을 결정하는 정책 인터페이스

public interface SlotSelectionPolicy {

    DecisionSlot selectNext(HomeConversationState state);
}
