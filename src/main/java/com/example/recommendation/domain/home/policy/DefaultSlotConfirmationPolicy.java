// package: com.example.recommendation.domain.home.policy

package com.example.recommendation.domain.home.policy;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;

/**
 * 기본 슬롯 확정 정책 (MVP)
 *
 * - ANSWERED 상태만 승격 대상
 * - 슬롯별 최소 기준만 적용
 */
@Component
public class DefaultSlotConfirmationPolicy
        implements SlotConfirmationPolicy {

    @Override
    public boolean canConfirm(
            DecisionSlot slot,
            SlotState state
    ) {

        // ANSWERED만 승격 후보
        if (state.getStatus() != SlotStatus.ANSWERED) {
            return false;
        }

        Object value = state.getValue();
        if (value == null) {
            return false;
        }

        return switch (slot) {
            case PURPOSE, TARGET, CONTEXT -> true;

            case BUDGET -> value instanceof Integer;

            case CONSTRAINT, PREFERENCE -> true;

            default -> false;
        };
    }
}