package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.ai.GuideSuggestionAI;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

// GUIDE_SUGGESTION_AI를 호출해 방향 제안을 생성하는 서비스 래퍼

@Service
public class GuideSuggestionService {

    private final GuideSuggestionAI guideSuggestionAI;

    public GuideSuggestionService(
            GuideSuggestionAI guideSuggestionAI
    ) {
        this.guideSuggestionAI = guideSuggestionAI;
    }

    public String generateGuide(
            DecisionSlot slot,
            HomeConversationState state
    ) {
        return guideSuggestionAI.generateGuide(slot, state);
    }
}
