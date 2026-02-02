package com.example.recommendation;

import java.util.List;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.external.openai.OpenAiExplanationClient;

/**
 * 테스트용 OpenAI Explanation Client
 * - 실제 AI 호출 ❌
 * - 고정된 설명 문장 반환
 */
public class FakeOpenAiExplanationClient implements OpenAiExplanationClient {

    @Override
    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return "테스트용 설명 문장입니다.";
    }
}