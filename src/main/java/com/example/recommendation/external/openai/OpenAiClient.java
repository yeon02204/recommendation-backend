package com.example.recommendation.external.openai;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;


@Component
public class OpenAiClient {

    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return "임시 설명입니다";
    }
}
