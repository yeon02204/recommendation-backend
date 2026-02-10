package com.example.recommendation.domain.home.policy;

import com.example.recommendation.domain.home.state.HomeConversationState;

public interface ReadyConditionPolicy {

    boolean isReady(HomeConversationState state);
}
