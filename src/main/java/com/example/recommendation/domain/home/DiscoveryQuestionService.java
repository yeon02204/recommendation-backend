package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.ai.DiscoveryQuestionAI;
import com.example.recommendation.domain.home.policy.DiscoverySlotSelector;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

// DISCOVERY_QUESTION_AI를 호출해 질문을 생성하는 서비스 래퍼

@Service
public class DiscoveryQuestionService {

    private final DiscoverySlotSelector slotSelector;
    private final DiscoveryQuestionAI questionAI;

    public DiscoveryQuestionService(
            DiscoverySlotSelector slotSelector,
            DiscoveryQuestionAI questionAI
    ) {
        this.slotSelector = slotSelector;
        this.questionAI = questionAI;
    }

    /**
     * 다음 질문 1문장을 생성한다
     * 질문할 슬롯이 없으면 null 반환 (READY)
     */
    public String generate(HomeConversationState state) {

        DecisionSlot slot =
                slotSelector.selectNext(state);

        if (slot == null) {
            return null; // READY 신호
        }

        // 슬롯 상태 변경 (질문했다는 기록)
        state.getSlot(slot).markAsked();

        return questionAI.generateQuestion(slot, state);
    }
}
