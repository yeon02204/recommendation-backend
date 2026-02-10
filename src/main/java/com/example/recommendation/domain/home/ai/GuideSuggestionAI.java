package com.example.recommendation.domain.home.ai;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 사용자 응답이 "모르겠어요 / 아무거나요"일 때
 * 선택 '방향'만 제시하는 AI
 */
public interface GuideSuggestionAI {

    String generateGuide(
            DecisionSlot slot,
            HomeConversationState state
    );
}
