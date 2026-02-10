package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.ai.GuideSuggestionAI;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;
import com.example.recommendation.domain.home.state.HomeConversationState;

// GUIDE_SUGGESTION_AI를 호출해 방향 제안을 생성하는 서비스 래퍼

@Service
public class GuideSuggestionService {

    private final GuideSuggestionAI guideSuggestionAI;

    public GuideSuggestionService(GuideSuggestionAI guideSuggestionAI) {
        this.guideSuggestionAI = guideSuggestionAI;
    }

    /**
     * USER_UNKNOWN 상태의 슬롯이 있으면
     * 방향 제시 문장을 생성
     */
    public String generate(HomeConversationState state) {

        for (SlotState slotState : state.getAll().values()) {

            if (slotState.getStatus() == SlotStatus.USER_UNKNOWN) {

                DecisionSlot slot = slotState.getSlot();

                return guideSuggestionAI.generateSuggestion(
                        slot,
                        state
                );
            }
        }

        return null; // 해당 없음
    }
}