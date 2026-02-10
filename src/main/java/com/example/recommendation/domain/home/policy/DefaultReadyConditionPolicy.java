package com.example.recommendation.domain.home.policy;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 기본 READY 판정 정책 (고도화 기준)
 *
 * READY 조건:
 * - PURPOSE는 반드시 ANSWERED 이상
 * - TARGET 또는 CONTEXT 중 하나는 ANSWERED 이상
 */
@Component
public class DefaultReadyConditionPolicy
        implements ReadyConditionPolicy {

    @Override
    public boolean isReady(HomeConversationState state) {

        SlotState purpose = state.getSlot(DecisionSlot.PURPOSE);
        SlotState target = state.getSlot(DecisionSlot.TARGET);
        SlotState context = state.getSlot(DecisionSlot.CONTEXT);

        boolean purposeReady =
                isAnsweredOrConfirmed(purpose);

        boolean targetOrContextReady =
                isAnsweredOrConfirmed(target)
             || isAnsweredOrConfirmed(context);

        return purposeReady && targetOrContextReady;
    }

    private boolean isAnsweredOrConfirmed(SlotState slot) {
        SlotStatus status = slot.getStatus();
        return status == SlotStatus.ANSWERED
            || status == SlotStatus.CONFIRMED;
    }
}
