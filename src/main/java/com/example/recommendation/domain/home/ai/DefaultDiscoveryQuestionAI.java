package com.example.recommendation.domain.home.ai;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

// 슬롯별 고정 질문 템플릿으로 실제 질문 문장을 만드는 기본 구현체

@Service
public class DefaultDiscoveryQuestionAI
        implements DiscoveryQuestionAI {

    private static final Map<DecisionSlot, String> QUESTION_TEMPLATES =
            new EnumMap<>(DecisionSlot.class);

    static {
        QUESTION_TEMPLATES.put(
                DecisionSlot.TARGET,
                "누구를 위한 상품인가요?"
        );
        QUESTION_TEMPLATES.put(
                DecisionSlot.PURPOSE,
                "어떤 용도로 사용할 예정인가요?"
        );
        QUESTION_TEMPLATES.put(
                DecisionSlot.CONSTRAINT,
                "피하고 싶은 점이 있을까요?"
        );
        QUESTION_TEMPLATES.put(
                DecisionSlot.PREFERENCE,
                "선호하는 스타일이나 성향이 있을까요?"
        );
        QUESTION_TEMPLATES.put(
                DecisionSlot.BUDGET,
                "예산은 어느 정도로 생각하고 계신가요?"
        );
        QUESTION_TEMPLATES.put(
                DecisionSlot.CONTEXT,
                "어떤 상황에서 쓰실 예정인가요?"
        );
    }

    @Override
    public String generateQuestion(
            DecisionSlot slot,
            HomeConversationState state
    ) {
        return QUESTION_TEMPLATES.getOrDefault(
                slot,
                "조금 더 알려주실 수 있을까요?"
        );
    }
}
