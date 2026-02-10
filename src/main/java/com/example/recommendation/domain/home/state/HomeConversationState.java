package com.example.recommendation.domain.home.state;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;


/**
 * HOME 대화 상태 저장소
 *
 * - 슬롯 상태를 기억한다
 * - 다음 행동(DISCOVERY / GUIDE / READY)을 판단한다
 *
 * ❌ 문장 생성
 * ❌ AI 호출
 */
public class HomeConversationState {

    private final Map<DecisionSlot, SlotState> slots =
            new EnumMap<>(DecisionSlot.class);

    public HomeConversationState() {
        for (DecisionSlot slot : DecisionSlot.values()) {
            slots.put(slot, new SlotState(slot));
        }
    }

    /* =========================
     * 기본 접근
     * ========================= */

    public SlotState getSlot(DecisionSlot slot) {
        return slots.get(slot);
    }

    public boolean isConfirmed(DecisionSlot slot) {
        return slots.get(slot).isConfirmed();
    }

    public Map<DecisionSlot, SlotState> getAll() {
        return Map.copyOf(slots); // 🔥 보호
    }

    /* =========================
     * DISCOVERY 대상
     * ========================= */

    /** 아직 질문조차 안 한 슬롯 */
    public List<SlotState> getUnaskedSlots() {
        return slots.values().stream()
                .filter(SlotState::needsQuestion)
                .collect(Collectors.toList());
    }

    /* =========================
     * GUIDE 대상
     * ========================= */

    /** 사용자가 "모르겠어요" 한 슬롯 */
    public List<SlotState> getUnknownSlots() {
        return slots.values().stream()
                .filter(SlotState::needsGuide)
                .collect(Collectors.toList());
    }

    /* =========================
     * READY 판단
     * ========================= */

    /** 검색으로 넘어가도 되는지 */
    public boolean isReadyForSearch() {
        return slots.values().stream()
                .anyMatch(SlotState::isConfirmed);
    }

    /* =========================
     * 요약용
     * ========================= */

    public List<SlotState> getConfirmedSlots() {
        return slots.values().stream()
                .filter(SlotState::isConfirmed)
                .collect(Collectors.toList());
    }
}
