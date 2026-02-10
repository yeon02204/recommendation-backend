package com.example.recommendation.domain.home.answer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 기본 슬롯 귀속 정책 (MVP)
 *
 * [규칙]
 * 1. 질문 맥락 우선 - lastAskedSlot이 있으면 1차 귀속 대상
 * 2. 슬롯 점프는 명시적일 때만 - SecondarySignal에 명시된 경우만
 * 3. UNKNOWN은 실패가 아님 - USER_UNKNOWN 상태로 전이
 *
 * [절대 금지]
 * - 의미 분류 ❌
 * - 상태 변경 ❌
 */
@Component
public class DefaultSlotBindingPolicy implements SlotBindingPolicy {
    
    @Override
    public List<SlotUpdateCommand> decide(
            AnswerInterpretation interpretation,
            PendingQuestionContext questionContext,
            HomeConversationState state
    ) {
        
        List<SlotUpdateCommand> commands = new ArrayList<>();
        
        AnswerIntent intent = interpretation.getPrimaryIntent();
        String value = interpretation.getNormalizedValue();
        DecisionSlot lastAsked = questionContext.getLastAskedSlot();
        
        // 1. NOISE → 무시
        if (intent == AnswerIntent.NOISE) {
            return commands;
        }
        
        // 2. CONTEXT_SHIFT → 별도 처리 (여기서는 무시)
        if (intent == AnswerIntent.CONTEXT_SHIFT) {
            return commands;
        }
        
        // 3. REFUSAL → lastAskedSlot을 USER_UNKNOWN으로
        if (intent == AnswerIntent.REFUSAL) {
            if (lastAsked != null) {
                commands.add(SlotUpdateCommand.unknown(lastAsked));
            }
            return commands;
        }
        
        // 4. UNKNOWN → lastAskedSlot을 USER_UNKNOWN으로
        if (intent == AnswerIntent.UNKNOWN) {
            if (lastAsked != null) {
                commands.add(SlotUpdateCommand.unknown(lastAsked));
            }
            return commands;
        }
        
        // 5. ANSWER → 질문 맥락 우선
        if (intent == AnswerIntent.ANSWER) {
            
            // 5-1. lastAskedSlot이 있으면 우선 답변
            if (lastAsked != null) {
                commands.add(SlotUpdateCommand.answer(lastAsked, value));
            }
            
            // 5-2. SecondarySignals 처리 (명시적 키워드 점프)
            if (interpretation.hasSecondarySignals()) {
                for (SecondarySignal signal : interpretation.getSecondarySignals()) {
                    
                    DecisionSlot targetSlot = signal.getTargetSlot();
                    String signalValue = signal.getValue();
                    
                    // 이미 처리한 슬롯이면 스킵
                    if (targetSlot.equals(lastAsked)) {
                        continue;
                    }
                    
                    commands.add(SlotUpdateCommand.answer(
                            targetSlot,
                            signalValue
                    ));
                }
            }
        }
        
        return commands;
    }
}