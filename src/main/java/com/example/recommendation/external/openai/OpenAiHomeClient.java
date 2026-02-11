package com.example.recommendation.external.openai;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.home.answer.AnswerInterpretation;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * HOME 단계 AI 호출 클라이언트
 *
 * [역할]
 * - 질문 생성
 * - 가이드 제시
 * - 요약 생성
 * - 답변 해석
 *
 * [절대 금지]
 * - 판단 ❌
 * - 상태 변경 ❌
 * - 검색 ❌
 */
public interface OpenAiHomeClient {
    
    /**
     * 슬롯별 질문 생성
     * 
     * @param slot 질문할 슬롯
     * @param state 현재 대화 상태
     * @return 생성된 질문 문장
     */
    String generateQuestion(
            DecisionSlot slot,
            HomeConversationState state
    );
    
    /**
     * 슬롯별 가이드 제시
     * 
     * @param slot 가이드할 슬롯
     * @param state 현재 대화 상태
     * @return 생성된 가이드 문장
     */
    String generateGuide(
            DecisionSlot slot,
            HomeConversationState state
    );
    
    /**
     * READY 단계 요약 생성
     * 
     * @param criteria 병합된 조건
     * @return 요약 문장
     */
    String generateReadySummary(
            RecommendationCriteria criteria
    );
    
    /**
     * 사용자 답변 해석
     * 
     * @param userInput 사용자 입력
     * @param lastAskedSlot 마지막 질문 슬롯 (null 가능)
     * @return 해석 결과
     */
    AnswerInterpretation interpretAnswer(
            String userInput,
            DecisionSlot lastAskedSlot
    );
}