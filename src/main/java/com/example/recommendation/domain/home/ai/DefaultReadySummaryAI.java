package com.example.recommendation.domain.home.ai;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.state.HomeConversationState;

@Component
public class DefaultReadySummaryAI
        implements ReadySummaryAI {

    @Override
    public String summarize(HomeConversationState state) {

        // 🔥 지금은 더미
        // 다음 단계에서 슬롯 기반 요약으로 교체
        return "말씀해주신 조건을 바탕으로 상품을 찾아볼게요.";
    }
}
