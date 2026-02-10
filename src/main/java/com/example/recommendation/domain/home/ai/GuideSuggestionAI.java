package com.example.recommendation.domain.home.ai;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 사용자 응답이 "모르겠어요 / 아무거나요"일 때
 * 선택 '방향'만 제시하는 AI
 * 
 * 사용자가 답을 못 할 때 상담 방향 문장을 생성하는 AI 인터페이스
 */
public interface GuideSuggestionAI {

    /**
     * 사용자가 모른다고 했을 때
     * 선택 방향을 "방향"으로만 제시한다
     */
	String generateSuggestion(
            DecisionSlot slot,
            HomeConversationState state
    );
}
