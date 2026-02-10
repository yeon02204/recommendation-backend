// package: com.example.recommendation.domain.home.policy

package com.example.recommendation.domain.home.policy;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;

/**
 * ANSWERED → CONFIRMED 승격 여부 판단 정책
 *
 * [역할]
 * - 슬롯별로 현재 답변을
 *   검색/요약에 써도 되는 "확정 값"으로 올릴 수 있는지 판단
 *
 * [절대 금지]
 * - 상태 변경 ❌
 * - 값 파싱 ❌
 * - AI 호출 ❌
 */
public interface SlotConfirmationPolicy {

    boolean canConfirm(
            DecisionSlot slot,
            SlotState state
    );
}
