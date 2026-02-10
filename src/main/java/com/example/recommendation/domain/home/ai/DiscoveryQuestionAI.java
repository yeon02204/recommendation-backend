package com.example.recommendation.domain.home.ai;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

public interface DiscoveryQuestionAI {

    /**
     * 선택된 슬롯 1개에 대해
     * 사용자에게 던질 질문 1문장을 생성한다
     */
    String generateQuestion(
            DecisionSlot slot,
            HomeConversationState state
    );
}
