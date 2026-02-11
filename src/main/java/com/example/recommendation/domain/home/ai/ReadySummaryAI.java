package com.example.recommendation.domain.home.ai;

import com.example.recommendation.domain.home.prompt.HomeReadySummaryPrompt;

/**
 * READY 요약 생성 AI 인터페이스
 *
 * [역할]
 * - 프롬프트 → 문장 변환만
 *
 * [절대 금지]
 * - READY 판단 ❌
 * - 슬롯/상태 접근 ❌
 * - 조건 추가 ❌
 */
public interface ReadySummaryAI {
    
    /**
     * READY 요약 생성
     * 
     * @param prompt 요약용 프롬프트
     * @return 요약 문장
     */
    String generate(HomeReadySummaryPrompt prompt);
}