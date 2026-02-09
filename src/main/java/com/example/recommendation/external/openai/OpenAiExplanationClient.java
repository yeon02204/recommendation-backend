package com.example.recommendation.external.openai;

import java.util.List;
import java.util.Map;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.explanation.CardExplanationPrompt;

public interface OpenAiExplanationClient {

    // 상단 설명
    String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    );

    // 카드별 설명 (신규)
    Map<Long, String> generateCardExplanations(
            List<CardExplanationPrompt> prompts,
            RecommendationCriteria criteria
    );
}
