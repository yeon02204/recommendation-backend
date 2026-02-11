package com.example.recommendation.domain.home.ai;

import com.example.recommendation.domain.home.state.HomeConversationState;

public interface SlotToKeywordAI {

    /**
     * CONFIRMED 슬롯 기반 검색 키워드 생성
     * 실패 시 null 반환
     */
    String generate(HomeConversationState state);
}
