package com.example.recommendation.domain.home.ai;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;
// 일반적인 선택 흐름을 바탕으로 방향 제안 문장을 만드는 기본 구현체

@Service
public class DefaultGuideSuggestionAI
        implements GuideSuggestionAI {

    private static final Map<DecisionSlot, String> GUIDE_MAP =
            new EnumMap<>(DecisionSlot.class);

    static {
        GUIDE_MAP.put(
                DecisionSlot.TARGET,
                "보통 누구를 위한 상품인지부터 정하면 선택이 쉬워져요."
        );
        GUIDE_MAP.put(
                DecisionSlot.PURPOSE,
                "사용 목적을 기준으로 고르는 경우가 많아요."
        );
        GUIDE_MAP.put(
                DecisionSlot.BUDGET,
                "예산 범위를 정해두면 선택지가 정리돼요."
        );
    }

    @Override
    public String generateSuggestion(
            DecisionSlot slot,
            HomeConversationState state
    ) {
        return GUIDE_MAP.getOrDefault(
                slot,
                "보통 많이 고려하는 기준부터 정해볼까요?"
        );
    }
}
