package com.example.recommendation.domain.explanation;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.external.openai.OpenAiExplanationClient;

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

    private final OpenAiExplanationClient openAiClient;

    public ExplanationService(OpenAiExplanationClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    /**
     * 추천 결과 설명 문장 생성 (RECOMMEND 전용)
     * - AI 호출은 여기서만 수행
     */
    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return openAiClient.generateExplanation(products, criteria);
    }

    /**
     * 정책 기반 고정 설명 문장 반환 (REQUERY / INVALID 전용)
     * - AI 호출 ❌
     * - ExplanationPolicy에 정의된 문장만 반환
     */
    public String generateByPolicy(ExplanationPolicy policy) {
        return policy.getMessage();
    }
}
