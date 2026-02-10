package com.example.recommendation.domain.home.ai;

import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 검색 직전 요약 전용 AI
 *
 * 역할
 * - 질문 ❌
 * - 상담 ❌
 * - 조건 정리 ⭕
 * - 이제 검색으로 간다는 신호 ⭕
 */
public interface ReadySummaryAI {

    String summarize(HomeConversationState state);
}
