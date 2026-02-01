package com.example.recommendation.domain.explanation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.external.openai.OpenAiClient;

/**
 * [역할]
 * - 추천 결과에 대한 "설명 문장"만 생성한다.
 *
 * [중요]
 * - AI 사용은 여기서만 허용
 * - Response DTO 생성 ❌
 * - Item / productId 조립 ❌
 * - 외부 계약 인지 ❌
 */
@Service
public class ExplanationService {

    private final OpenAiClient openAiClient;

    public ExplanationService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    /**
     * 추천 결과 설명 문장 생성
     */
    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return openAiClient.generateExplanation(products, criteria);
    }
}
