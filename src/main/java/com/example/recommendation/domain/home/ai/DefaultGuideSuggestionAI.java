package com.example.recommendation.domain.home.ai;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

@Component
public class DefaultGuideSuggestionAI
        implements GuideSuggestionAI {

    @Override
    public String generateGuide(
            DecisionSlot slot,
            HomeConversationState state
    ) {

        return switch (slot) {
            case TARGET ->
                    "보통 누구를 위한 상품인지에 따라 추천이 많이 달라져요. 대상부터 정해볼까요?";
            case PURPOSE ->
                    "사용 목적에 따라 선택지가 크게 달라져요. 어떤 용도로 쓰실 예정인가요?";
            case CONSTRAINT ->
                    "피하고 싶은 조건이 있으면 먼저 정리하는 게 좋아요.";
            case PREFERENCE ->
                    "선호하는 스타일이나 성향이 있으면 추천이 쉬워져요.";
            case BUDGET ->
                    "예산 범위를 정해두면 선택이 훨씬 편해져요.";
            case CONTEXT ->
                    "어떤 상황에서 사용할 건지 알려주실 수 있을까요?";
        };
    }
}
