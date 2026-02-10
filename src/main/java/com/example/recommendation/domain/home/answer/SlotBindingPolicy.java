package com.example.recommendation.domain.home.answer;

import java.util.List;

import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 슬롯 귀속 정책 인터페이스
 *
 * [역할]
 * - AnswerInterpretation + PendingQuestionContext
 * → 어떤 슬롯을 어떻게 업데이트할지 결정
 *
 * [절대 금지]
 * - 의미 분류 ❌
 * - 상태 변경 ❌
 * - AI 호출 ❌
 */
public interface SlotBindingPolicy {
    
    /**
     * 슬롯 업데이트 명령 생성
     *
     * @param interpretation 발화 해석 결과
     * @param questionContext 질문 맥락
     * @param state 현재 대화 상태
     * @return 슬롯 업데이트 명령 리스트
     */
    List<SlotUpdateCommand> decide(
            AnswerInterpretation interpretation,
            PendingQuestionContext questionContext,
            HomeConversationState state
    );
}