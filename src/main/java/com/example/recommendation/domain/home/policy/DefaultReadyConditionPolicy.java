package com.example.recommendation.domain.home.policy;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.answer.PendingQuestionContext;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 기본 READY 판정 정책 (고도화 기준)
 *
 * READY 조건:
 * - PURPOSE는 반드시 CONFIRMED
 * - TARGET 또는 CONTEXT 중 하나는 ANSWERED 이상
 *
 * + STEP 11
 * - UNKNOWN → GUIDE → UNKNOWN 루프 차단
 */
@Component
public class DefaultReadyConditionPolicy
        implements ReadyConditionPolicy {

    @Override
    public boolean isReady(HomeConversationState state) {

        PendingQuestionContext ctx = state.getQuestionContext();

     // STEP 11 : GUIDE / UNKNOWN 루프 탈출
        if (ctx.getLastAskedSlot() != null) {

            if (ctx.isLastQuestionAnswered()
                    && ctx.wasLastAnswerUnknown()
                    && ctx.recentlyGuided()
                    && ctx.wasLastGuide(ctx.getLastAskedSlot())) {

                return true;
            }
        }


        /* =========================
         * 정상 READY 판정
         * ========================= */

        SlotState purpose = state.getSlot(DecisionSlot.PURPOSE);
        SlotState target = state.getSlot(DecisionSlot.TARGET);
        SlotState context = state.getSlot(DecisionSlot.CONTEXT);

        boolean purposeReady =
                purpose.getStatus() == SlotStatus.CONFIRMED;

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
