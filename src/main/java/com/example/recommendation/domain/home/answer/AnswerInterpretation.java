package com.example.recommendation.domain.home.answer;

import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 발화 해석 결과
 *
 * [역할]
 * - 주 의도(PrimaryIntent) + 추가 신호(SecondarySignals) 보관
 * - 정제된 값(normalizedValue) 보관
 *
 * [절대 금지]
 * - 슬롯 판단 ❌
 * - 상태 변경 ❌
 */
public class AnswerInterpretation {
    
    private final AnswerIntent primaryIntent;
    private final String normalizedValue;
    private final List<SecondarySignal> secondarySignals;
    
    public AnswerInterpretation(
            AnswerIntent primaryIntent,
            String normalizedValue
    ) {
        this.primaryIntent = primaryIntent;
        this.normalizedValue = normalizedValue;
        this.secondarySignals = new ArrayList<>();
    }
    
    public AnswerInterpretation(
            AnswerIntent primaryIntent,
            String normalizedValue,
            List<SecondarySignal> secondarySignals
    ) {
        this.primaryIntent = primaryIntent;
        this.normalizedValue = normalizedValue;
        this.secondarySignals = secondarySignals != null 
                ? new ArrayList<>(secondarySignals) 
                : new ArrayList<>();
    }
    
    public AnswerIntent getPrimaryIntent() {
        return primaryIntent;
    }
    
    public String getNormalizedValue() {
        return normalizedValue;
    }
    
    public List<SecondarySignal> getSecondarySignals() {
        return secondarySignals;
    }
    
    public boolean hasSecondarySignals() {
        return secondarySignals != null && !secondarySignals.isEmpty();
    }
}