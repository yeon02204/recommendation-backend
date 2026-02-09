package com.example.recommendation.external.openai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.explanation.CardExplanationPrompt;
import com.example.recommendation.domain.explanation.CardRole;

/**
 * 테스트용 OpenAI Explanation Client
 * - 실제 AI 호출 ❌
 * - 카드별 설명 로직 검증용
 */
public class FakeOpenAiExplanationClient implements OpenAiExplanationClient {

    /**
     * 상단 공통 설명 (기존 유지)
     */
    @Override
    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return "사용자 조건을 기준으로 적합한 상품들을 골라봤어요.";
    }

    /**
     * 카드별 설명 생성 (핵심)
     */
    @Override
    public Map<Long, String> generateCardExplanations(
            List<CardExplanationPrompt> prompts,
            RecommendationCriteria criteria
    ) {
        Map<Long, String> result = new HashMap<>();

        for (CardExplanationPrompt p : prompts) {
            CardRole role = decideRole(p);
            String message = messageByRole(role);
            result.put(p.productId(), message);
        }

        return result;
    }

    /**
     * 카드 역할 결정
     * - 점수 ❌
     * - 순위 ❌
     * - 오직 "설명 톤"만 결정
     */
    private CardRole decideRole(CardExplanationPrompt p) {

        int optionCount = p.matchedOptionKeywords().size();

        if (optionCount >= 2) {
            return CardRole.OPTION_STRONG;
        }

        if (p.brandMatched()) {
            return CardRole.BRAND_TRUST;
        }

        if (optionCount == 1) {
            return CardRole.SINGLE_FOCUS;
        }

        return CardRole.SAFE_CHOICE;
    }

    /**
     * 역할 → 사용자 노출 문장
     */
    private String messageByRole(CardRole role) {
        return switch (role) {
            case OPTION_STRONG ->
                "여러 조건을 자연스럽게 만족하는 제품이에요.";

            case BRAND_TRUST ->
                "선호하신 브랜드 기준으로 안정적인 선택이에요.";

            case SINGLE_FOCUS ->
                "가장 중요하게 보신 조건에 잘 맞는 제품이에요.";

            case SAFE_CHOICE ->
                "전반적으로 무난하게 선택하기 좋은 제품이에요.";
        };
    }
}
