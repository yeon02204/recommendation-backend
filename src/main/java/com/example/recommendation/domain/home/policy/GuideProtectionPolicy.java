package com.example.recommendation.domain.home.policy;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * GUIDE 노출 보호 정책
 *
 * [역할]
 * - GUIDE를 보여줘도 되는지 여부만 판단
 *
 * [절대 금지]
 * - 상태 변경 ❌
 * - 질문 선택 ❌
 */
public interface GuideProtectionPolicy {

    boolean allowGuide(
            DecisionSlot slot,
            HomeConversationState state
    );
}
