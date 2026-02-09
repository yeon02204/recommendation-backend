package com.example.recommendation.domain.explanation;

import java.util.List;

/**
 * 카드별 설명 생성을 위한 AI 프롬프트 캐리어
 * - 내부 점수 / 정책 노출 ❌
 * - AI 힌트 전용
 */
public record CardExplanationPrompt(
        Long productId,
        String title,
        List<String> matchedOptionKeywords,
        boolean brandMatched
) {}
