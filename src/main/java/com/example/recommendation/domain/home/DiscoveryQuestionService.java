package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.ai.DiscoveryQuestionAI;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotPriorityPolicy;
import com.example.recommendation.domain.home.state.HomeConversationState;

// DISCOVERY_QUESTION_AI를 호출해 질문을 생성하는 서비스 래퍼

@Service
public class DiscoveryQuestionService {

    private final SlotPriorityPolicy slotPriorityPolicy;
    private final DiscoveryQuestionAI discoveryQuestionAI;

    public DiscoveryQuestionService(
            SlotPriorityPolicy slotPriorityPolicy,
            DiscoveryQuestionAI discoveryQuestionAI
    ) {
        this.slotPriorityPolicy = slotPriorityPolicy;
        this.discoveryQuestionAI = discoveryQuestionAI;
    }

    public String generateQuestion(HomeConversationState state) {

        DecisionSlot slot =
                slotPriorityPolicy.nextSlot(state);

        if (slot == null) {
            return null; // READY로 넘어가야 함
        }

        state.getSlot(slot).markAsked();

        return discoveryQuestionAI.generateQuestion(
                slot,
                state
        );
    }
}
