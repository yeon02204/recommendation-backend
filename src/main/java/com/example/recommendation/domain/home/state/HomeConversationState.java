package com.example.recommendation.domain.home.state;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.answer.PendingQuestionContext;
import com.example.recommendation.domain.home.answer.SlotUpdateCommand;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotAnswer;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.slot.SlotStatus;


/**
 * HOME 대화 상태 저장소
 *
 * - 슬롯 상태를 기억한다
 * - 질문 맥락을 추적한다 (STEP 10)
 * - 다음 행동(DISCOVERY / GUIDE / READY)을 판단한다
 *
 * ❌ 문장 생성
 * ❌ AI 호출
 * 
 * HOME 단계 전체 슬롯 상태를 보관하는 대화 상태 컨테이너
 */
@Component
public class HomeConversationState {

    private final Map<DecisionSlot, SlotState> slots =
            new EnumMap<>(DecisionSlot.class);
    
    // 🔥 STEP 10: 질문 맥락 추적
    private final PendingQuestionContext questionContext =
            new PendingQuestionContext();

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
        return Map.copyOf(slots);
    }
    
    /**
     * 🔥 STEP 10: 질문 맥락 조회
     */
    public PendingQuestionContext getQuestionContext() {
        return questionContext;
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
    
    /**
     * 🔥 STEP 10: SlotUpdateCommand 적용
     */
    public void apply(SlotUpdateCommand command) {
        
        SlotState slotState = slots.get(command.getSlot());
        SlotStatus targetStatus = command.getTargetStatus();
        Object value = command.getValue();
        
        switch (targetStatus) {
            case ANSWERED -> slotState.answer(value);
            case USER_UNKNOWN -> slotState.markUserUnknown();
            case CONFIRMED -> slotState.confirm(value);
            case ASKED -> slotState.markAsked();
            default -> {
                // EMPTY는 무시 (초기 상태)
            }
        }
    }
    
    /**
     * 🔥 STEP 10: 여러 명령 일괄 적용
     */
    public void applyAll(List<SlotUpdateCommand> commands) {
        for (SlotUpdateCommand command : commands) {
            apply(command);
        }
    }
    
    /**
     * 기존 호환 메서드 (유지)
     */
    public void applyAnswer(
            DecisionSlot slot,
            SlotAnswer answer
    ) {
        SlotState s = slots.get(slot);

        if (answer.getStatus() == SlotStatus.USER_UNKNOWN) {
            s.markUserUnknown();
            return;
        }

        if (answer.getStatus() == SlotStatus.ANSWERED) {
            s.answer(answer.getValue());
        }
    }

    public String describeConfirmedSlots() {

        StringBuilder sb = new StringBuilder();

        for (DecisionSlot slot : DecisionSlot.values()) {

            SlotState slotState = getSlot(slot);

            if (slotState != null
                    && slotState.isConfirmed()
                    && slotState.getValue() != null) {

                sb.append("- ")
                  .append(slot.name())
                  .append(": ")
                  .append(slotState.getValue())
                  .append("\n");
            }
        }

        if (sb.length() == 0) {
            return "없음";
        }

        return sb.toString();
    }


}