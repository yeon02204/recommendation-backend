package com.example.recommendation.domain.home.answer;

import com.example.recommendation.domain.home.slot.DecisionSlot;

/**
 * 질문 맥락 추적기
 *
 * [역할]
 * - 현재 어떤 슬롯에 대해 질문했는지
 * - 사용자가 답했는지
 * - 명시적 키워드 점프가 있었는지
 * 추적
 *
 * [절대 금지]
 * - 판단 ❌
 * - 상태 변경 ❌
 */
public class PendingQuestionContext {
    
    private DecisionSlot lastAskedSlot;
    private DecisionSlot lastJumpedSlot;
    private boolean lastQuestionAnswered;
    
    public PendingQuestionContext() {
        this.lastQuestionAnswered = false;
    }
    
    /**
     * 새 질문 시작
     */
    public void markAsked(DecisionSlot slot) {
        this.lastAskedSlot = slot;
        this.lastJumpedSlot = null;
        this.lastQuestionAnswered = false;
    }
    
    /**
     * 질문에 답변함
     */
    public void markAnswered() {
        this.lastQuestionAnswered = true;
    }
    
    /**
     * 명시적 키워드 점프
     */
    public void markJumped(DecisionSlot jumpedSlot) {
        this.lastJumpedSlot = jumpedSlot;
        // 질문에는 답 안함
        this.lastQuestionAnswered = false;
    }
    
    /**
     * 맥락 초기화
     */
    public void reset() {
        this.lastAskedSlot = null;
        this.lastJumpedSlot = null;
        this.lastQuestionAnswered = false;
    }
    
    // Getters
    
    public DecisionSlot getLastAskedSlot() {
        return lastAskedSlot;
    }
    
    public DecisionSlot getLastJumpedSlot() {
        return lastJumpedSlot;
    }
    
    public boolean isLastQuestionAnswered() {
        return lastQuestionAnswered;
    }
    
    public boolean hasUnansweredQuestion() {
        return lastAskedSlot != null && !lastQuestionAnswered;
    }
}