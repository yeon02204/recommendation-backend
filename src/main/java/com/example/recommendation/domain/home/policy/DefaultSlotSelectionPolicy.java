package com.example.recommendation.domain.home.policy;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;
import com.example.recommendation.domain.home.state.HomeConversationState;

// 슬롯 우선순위 정책을 실제 선택 로직에 적용하는 기본 정책 구현체

@Component
public class DefaultSlotSelectionPolicy
        implements SlotSelectionPolicy {

    private static final List<DecisionSlot> PRIORITY = List.of(
            DecisionSlot.TARGET,
            DecisionSlot.PURPOSE,
            DecisionSlot.CONSTRAINT,
            DecisionSlot.PREFERENCE,
            DecisionSlot.BUDGET,
            DecisionSlot.CONTEXT
    );

    private final GuideProtectionPolicy guideProtectionPolicy;

    public DefaultSlotSelectionPolicy(
            GuideProtectionPolicy guideProtectionPolicy
    ) {
        this.guideProtectionPolicy = guideProtectionPolicy;
    }

    @Override
    public DecisionSlot selectNext(HomeConversationState state) {

        // 1️⃣ USER_UNKNOWN → 가이드 대상
        for (DecisionSlot slot : PRIORITY) {
            SlotState s = state.getSlot(slot);
            if (s.getStatus() == SlotStatus.USER_UNKNOWN) {
                return slot;
            }
        }

        // 2️⃣ EMPTY → 질문 대상
        for (DecisionSlot slot : PRIORITY) {
            SlotState s = state.getSlot(slot);
            if (s.getStatus() == SlotStatus.EMPTY) {
                return slot;
            }
        }

        // 3️⃣ 더 이상 물을 게 없음
        return null;
    }

    @Override
    public DecisionSlot selectGuideTarget(HomeConversationState state) {

        for (DecisionSlot slot : PRIORITY) {
            SlotState s = state.getSlot(slot);

            if (s.getStatus() == SlotStatus.USER_UNKNOWN) {

                // 🔥 STEP 11: GUIDE 보호 정책 적용
                if (!guideProtectionPolicy.allowGuide(slot, state)) {
                    continue;
                }

                return slot;
            }
        }
        return null;
    }
}
