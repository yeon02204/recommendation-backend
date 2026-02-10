package com.example.recommendation.domain.home.slot;

public class SlotState {

    private final DecisionSlot slot;
    private SlotStatus status;
    private Object value; // String / Integer / Enum 등

    public SlotState(DecisionSlot slot) {
        this.slot = slot;
        this.status = SlotStatus.EMPTY;
    }

    public DecisionSlot getSlot() {
        return slot;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public Object getValue() {
        return value;
    }

    /* =========================
     * 상태 전이 메서드
     * ========================= */

    /** 질문을 처음 던질 때만 */
    public void markAsked() {
        if (this.status == SlotStatus.EMPTY) {
            this.status = SlotStatus.ASKED;
        }
    }

    /** 사용자가 "모르겠어요"라고 한 경우 */
    public void markUserUnknown() {
        this.status = SlotStatus.USER_UNKNOWN;
    }

    /** 답변은 받았지만 아직 확정은 아님 */
    public void answer(Object value) {
        this.status = SlotStatus.ANSWERED;
        this.value = value;
    }

    /** 검색/요약에 써도 되는 확정 값 */
    public void confirm(Object value) {
        this.status = SlotStatus.CONFIRMED;
        this.value = value;
    }

    /* =========================
     * 편의 메서드
     * ========================= */

    public boolean isConfirmed() {
        return this.status == SlotStatus.CONFIRMED;
    }

    public boolean needsQuestion() {
        return this.status == SlotStatus.EMPTY;
    }

    public boolean needsGuide() {
        return this.status == SlotStatus.USER_UNKNOWN;
    }
}
