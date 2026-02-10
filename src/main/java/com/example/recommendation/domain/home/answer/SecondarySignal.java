package com.example.recommendation.domain.home.answer;

import com.example.recommendation.domain.home.slot.DecisionSlot;

/**
 * 주 의도 외 추가 신호
 *
 * [역할]
 * - "친구 결혼인데 비싼 건 부담스러워요" 같은 복합 발화 처리
 * - 여러 정보가 섞인 경우 추가 정보 추출
 *
 * [사용 예]
 * - 주 의도: ANSWER (친구 결혼)
 * - 추가 신호: CONSTRAINT (비싸면 안됨)
 */
public class SecondarySignal {
    
    private final DecisionSlot targetSlot;
    private final String value;
    
    public SecondarySignal(DecisionSlot targetSlot, String value) {
        this.targetSlot = targetSlot;
        this.value = value;
    }
    
    public DecisionSlot getTargetSlot() {
        return targetSlot;
    }
    
    public String getValue() {
        return value;
    }
}