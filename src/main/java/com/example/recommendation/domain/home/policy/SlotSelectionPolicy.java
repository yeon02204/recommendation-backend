package com.example.recommendation.domain.home.policy;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

public interface SlotSelectionPolicy {

    DecisionSlot selectNext(HomeConversationState state);
}
