package com.example.recommendation.domain.home.ai;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.prompt.HomeReadySummaryPrompt;

/**
 * Fallback READY 요약 생성 구현체
 *
 * [역할]
 * - OpenAI 실패 시 사용
 * - 고정 문장
 */
@Component
public class FallbackReadySummaryAI implements ReadySummaryAI {
    
    @Override
    public String generate(HomeReadySummaryPrompt prompt) {
        return "말씀해주신 조건을 바탕으로 상품을 찾아볼게요.";
    }
}