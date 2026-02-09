package com.example.recommendation.domain.recommendation;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.explanation.CardExplanationPrompt;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.external.naver.dto.Product;

/**
 * RecommendationAssembler
 *
 * [역할]
 * - Evaluation 결과를 Response Item으로 조립
 *
 * [책임]
 * - 카드별 explanation 연결
 * - DTO 변환
 *
 * [금지]
 * - 판단 ❌
 * - 검색 ❌
 * - 정책 결정 ❌
 */
@Component
public class RecommendationAssembler {

    private final ExplanationService explanationService;

    public RecommendationAssembler(
            ExplanationService explanationService
    ) {
        this.explanationService = explanationService;
    }

    public List<RecommendationResponseDto.Item> assembleItems(
            List<EvaluatedProduct> evaluatedProducts,
            RecommendationCriteria criteria
    ) {
        if (evaluatedProducts == null || evaluatedProducts.isEmpty()) {
            return List.of();
        }

        // 1️⃣ 카드 설명 프롬프트 생성
        List<CardExplanationPrompt> prompts =
                evaluatedProducts.stream()
                        .map(p -> new CardExplanationPrompt(
                                p.getProduct().getId(),
                                p.getProduct().getTitle(),
                                p.getMatchedOptionKeywords(),
                                p.hasBrandMatch()
                        ))
                        .toList();

        // 2️⃣ 카드 설명 생성
        Map<Long, String> explanations =
                explanationService.generateCardExplanations(
                        prompts,
                        criteria
                );

        // 3️⃣ Item 변환
        return evaluatedProducts.stream()
                .map(p -> toItem(
                        p.getProduct(),
                        explanations.get(p.getProduct().getId())
                ))
                .collect(Collectors.toList());
    }

    private RecommendationResponseDto.Item toItem(
            Product product,
            String explanation
    ) {
        return new RecommendationResponseDto.Item(
                product.getId(),
                product.getTitle(),
                product.getImageUrl(),
                product.getLink(),
                product.getPrice(),
                product.getBrand() != null ? product.getBrand() : "기타",
                explanation != null ? explanation : ""
        );
    }
}
