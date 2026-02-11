package com.example.recommendation.domain.home.policy;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

@Component
public class DefaultGuideProtectionPolicy
        implements GuideProtectionPolicy {

    @Override
    public boolean allowGuide(
            DecisionSlot slot,
            HomeConversationState state
    ) {

        var context = state.getQuestionContext();

        // 1️⃣ 같은 슬롯으로 연속 GUIDE 금지
        if (context.wasLastGuide(slot)) {
            return false;
        }

        // 2️⃣ 질문 이후 GUIDE가 과도하게 반복되면 차단 (STEP 11 핵심)
        // 질문 → GUIDE → GUIDE (2회 초과) 방지
        if (context.tooManyGuidesSinceLastQuestion()) {
            return false;
        }

        // ✅ UNKNOWN 자체는 GUIDE 허용 조건
        return true;
    }
}
