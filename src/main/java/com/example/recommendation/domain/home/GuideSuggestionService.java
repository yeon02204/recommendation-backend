package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.ai.GuideSuggestionAI;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

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
