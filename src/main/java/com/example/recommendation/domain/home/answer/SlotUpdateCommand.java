package com.example.recommendation.domain.home.answer;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotStatus;

/**
 * 슬롯 상태 업데이트 명령
 *
 * [역할]
 * - 어떤 슬롯을
 * - 어떤 상태로
 * - 어떤 값으로
 * 변경할지 담는 명령 객체
 *
 * [절대 금지]
 * - 판단 ❌
 * - 직접 실행 ❌
 */
public class SlotUpdateCommand {
    
    private final DecisionSlot slot;
    private final SlotStatus targetStatus;
    private final Object value;
    
    public SlotUpdateCommand(
            DecisionSlot slot,
            SlotStatus targetStatus,
            Object value
    ) {
        this.slot = slot;
        this.targetStatus = targetStatus;
        this.value = value;
    }
    
    public DecisionSlot getSlot() {
        return slot;
    }
    
    public SlotStatus getTargetStatus() {
        return targetStatus;
    }
    
    public Object getValue() {
        return value;
    }
    
    /**
     * 편의 생성자 - ANSWERED
     */
    public static SlotUpdateCommand answer(DecisionSlot slot, Object value) {
        return new SlotUpdateCommand(slot, SlotStatus.ANSWERED, value);
    }
    
    /**
     * 편의 생성자 - USER_UNKNOWN
     */
    public static SlotUpdateCommand unknown(DecisionSlot slot) {
        return new SlotUpdateCommand(slot, SlotStatus.USER_UNKNOWN, null);
    }
    
    /**
     * 편의 생성자 - CONFIRMED
     */
    public static SlotUpdateCommand confirm(DecisionSlot slot, Object value) {
        return new SlotUpdateCommand(slot, SlotStatus.CONFIRMED, value);
    }
}