package com.example.recommendation.external.openai;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;

// open Ai가 자연어에서 키워드 옵션 등등을 뽑아내도록 프롬프트를 작성하는곳
@Component
public class OpenAiClient {

    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return "임시 설명입니다";
    }
}
