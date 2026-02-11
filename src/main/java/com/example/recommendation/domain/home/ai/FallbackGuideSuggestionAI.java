package com.example.recommendation.domain.home.ai;

import java.util.EnumMap;
import java.util.Map;


import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * Fallback 가이드 제시 구현체
 * 
 * [역할]
 * - OpenAI 실패 시 사용
 * - 고정 템플릿
 */
@Service
public class FallbackGuideSuggestionAI implements GuideSuggestionAI {

    private static final Map<DecisionSlot, String> GUIDE_TEMPLATES =
            new EnumMap<>(DecisionSlot.class);

    static {
        GUIDE_TEMPLATES.put(
                DecisionSlot.TARGET,
                "보통 누구를 위한 상품인지부터 정하면 선택이 쉬워져요. 어떤 분께 필요한 걸까요?"
        );
        GUIDE_TEMPLATES.put(
                DecisionSlot.PURPOSE,
                "사용 목적을 정하면 추천 범위를 많이 줄일 수 있어요. 어떤 용도로 생각 중이세요?"
        );
        GUIDE_TEMPLATES.put(
                DecisionSlot.CONSTRAINT,
                "피하고 싶은 조건이 있으면 먼저 정하는 것도 좋아요."
        );
        GUIDE_TEMPLATES.put(
                DecisionSlot.PREFERENCE,
                "스타일이나 취향을 기준으로 방향을 잡아볼 수도 있어요."
        );
        GUIDE_TEMPLATES.put(
                DecisionSlot.BUDGET,
                "대략적인 예산 범위를 정하면 선택이 훨씬 쉬워져요."
        );
        GUIDE_TEMPLATES.put(
                DecisionSlot.CONTEXT,
                "어떤 상황에서 쓰는지에 따라 추천 방향이 달라질 수 있어요."
        );
    }

    @Override
    public String generateSuggestion(
            DecisionSlot slot,
            HomeConversationState state
    ) {
        return GUIDE_TEMPLATES.getOrDefault(
                slot,
                "이런 방향으로 한 번 생각해보는 건 어떠세요?"
        );
    }
}