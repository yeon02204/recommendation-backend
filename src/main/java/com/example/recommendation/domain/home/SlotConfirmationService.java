package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.policy.SlotConfirmationPolicy;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * ANSWERED → CONFIRMED 승격 전용 서비스
 *
 * [역할]
 * - HomeConversationState의 모든 슬롯을 순회
 * - SlotConfirmationPolicy로 승격 가능 여부 판단
 * - 가능하면 confirm() 호출
 *
 * [절대 금지]
 * - 판단 정책 직접 구현 ❌
 * - AI 호출 ❌
 * - 검색 ❌
 */
@Service
public class SlotConfirmationService {

    private final SlotConfirmationPolicy confirmationPolicy;

    public SlotConfirmationService(
            SlotConfirmationPolicy confirmationPolicy
    ) {
        this.confirmationPolicy = confirmationPolicy;
    }

    /**
     * ANSWERED 상태 슬롯 중 승격 가능한 것을 CONFIRMED로 전이
     */
    public void promoteAnsweredSlots(HomeConversationState state) {

        for (DecisionSlot slot : DecisionSlot.values()) {
            SlotState slotState = state.getSlot(slot);

            if (confirmationPolicy.canConfirm(slot, slotState)) {
                slotState.confirm(slotState.getValue());
            }
        }
    }
}