package com.example.recommendation.domain.home.policy;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * DISCOVERY 단계에서
 * "지금 가장 먼저 물어봐야 할 슬롯"을 선택하는 판단자
 *
 * ❌ 문장 생성
 * ❌ AI 호출
 * 
 * DISCOVERY 단계 전용 슬롯 선택 책임자 인터페이스
 */
public interface DiscoverySlotSelector {

    DecisionSlot select(HomeConversationState state);
}
