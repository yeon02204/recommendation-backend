package com.example.recommendation.domain.home.answer;

import com.example.recommendation.domain.home.slot.DecisionSlot;

/**
 * 질문 맥락 추적기
 *
 * [역할]
 * - 현재 어떤 슬롯에 대해 질문했는지
 * - 사용자가 답했는지
 * - 명시적 키워드 점프가 있었는지
 * - GUIDE 보호를 위한 최소 이력 추적
 *
 * [절대 금지]
 * - 판단 ❌
 * - 상태 변경 ❌
 */
public class PendingQuestionContext {

    private DecisionSlot lastAskedSlot;
    private DecisionSlot lastJumpedSlot;
    private boolean lastQuestionAnswered;

    // 🔽 STEP 11용 최소 필드 (기존 유지)
    private DecisionSlot lastGuidedSlot;
    private boolean lastAnswerUnknown;
    private int guideCountSinceLastQuestion;

    public PendingQuestionContext() {
        this.lastQuestionAnswered = false;
        this.guideCountSinceLastQuestion = 0;
    }

    /* =========================
     * 질문 흐름
     * ========================= */

    /** 새 질문 시작 */
    public void markAsked(DecisionSlot slot) {
        this.lastAskedSlot = slot;
        this.lastJumpedSlot = null;
        this.lastQuestionAnswered = false;

        // 질문이 새로 시작되면 GUIDE 누적은 의미 없으므로 리셋
        this.guideCountSinceLastQuestion = 0;
        this.lastAnswerUnknown = false;
    }

    /** 질문에 정상 답변 */
    public void markAnswered() {
        this.lastQuestionAnswered = true;
        this.lastAnswerUnknown = false;
    }

    /** 질문에 답했지만 모름/회피 */
    public void markAnswerUnknown() {
        this.lastQuestionAnswered = true;
        this.lastAnswerUnknown = true;
    }

    /** 명시적 키워드 점프 */
    public void markJumped(DecisionSlot jumpedSlot) {
        this.lastJumpedSlot = jumpedSlot;
        this.lastQuestionAnswered = false;

        // 점프는 질문 흐름을 끊는 행위이므로 UNKNOWN 플래그 제거
        this.lastAnswerUnknown = false;
    }

    /** GUIDE 사용 기록 */
    public void markGuided(DecisionSlot slot) {
        this.lastGuidedSlot = slot;
        this.guideCountSinceLastQuestion++;
    }

    /** 맥락 초기화 */
    public void reset() {
        this.lastAskedSlot = null;
        this.lastJumpedSlot = null;
        this.lastQuestionAnswered = false;
        this.lastGuidedSlot = null;
        this.lastAnswerUnknown = false;
        this.guideCountSinceLastQuestion = 0;
    }

    /* =========================
     * STEP 11 조회용 메서드
     * (판단 ❌, 상태 변경 ❌)
     * ========================= */

    /** 직전에 같은 슬롯으로 GUIDE 했는가 */
    public boolean wasLastGuide(DecisionSlot slot) {
        return lastGuidedSlot == slot;
    }

    /** 직전 답변이 USER_UNKNOWN 이었는가 */
    public boolean wasLastAnswerUnknown() {
        return lastAnswerUnknown;
    }

    /** 최근 질문 이후 GUIDE가 1회 이상 있었는가 */
    public boolean recentlyGuided() {
        return guideCountSinceLastQuestion > 0;
    }

    /** 최근 질문 이후 GUIDE가 과도했는가 (STEP 11 기준) */
    public boolean tooManyGuidesSinceLastQuestion() {
        return guideCountSinceLastQuestion >= 2;
    }

    /* =========================
     * 기존 Getter (유지)
     * ========================= */

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
