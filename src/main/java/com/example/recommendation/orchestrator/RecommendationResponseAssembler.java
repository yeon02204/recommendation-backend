package com.example.recommendation.orchestrator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.explanation.CardExplanationPrompt;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.external.naver.dto.Product;

@Component
public class RecommendationResponseAssembler {

    private final ExplanationService explanationService;

    public RecommendationResponseAssembler(
            ExplanationService explanationService
    ) {
        this.explanationService = explanationService;
    }

    /* =========================
     * 메인 설명
     * ========================= */
    public String buildMainMessage(
            EvaluationResult evaluationResult,
            RecommendationCriteria criteria
    ) {
        if (evaluationResult.getCandidateCount() == 0) {
            return explanationService.generateByPolicy(
                    com.example.recommendation.domain.explanation.ExplanationPolicy
                            .REQUERY_NEED_MORE_CONDITION
            );
        }

        return explanationService.generateExplanation(
                evaluationResult.getProducts(),
                criteria
        );
    }

    /* =========================
     * 카드별 설명
     * ========================= */
    public Map<Long, String> buildCardExplanations(
            EvaluationResult evaluationResult,
            RecommendationCriteria criteria
    ) {
        if (evaluationResult.getCandidateCount() == 0) {
            return Map.of();
        }

        List<CardExplanationPrompt> prompts =
                evaluationResult.getProducts().stream()
                        .map(this::toPrompt)
                        .toList();

        return explanationService.generateCardExplanations(
                prompts,
                criteria
        );
    }

    private CardExplanationPrompt toPrompt(EvaluatedProduct p) {
        return new CardExplanationPrompt(
                p.getProduct().getId(),
                p.getProduct().getTitle(),
                p.getMatchedOptionKeywords(),
                p.hasBrandMatch()
        );
    }

    /* =========================
     * Item 조립
     * ========================= */
    public List<RecommendationResponseDto.Item> assembleItems(
            List<Product> products,
            Map<Long, String> cardExplanations
    ) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }

        return products.stream()
                .limit(5)
                .map(product -> new RecommendationResponseDto.Item(
                        product.getId(),
                        product.getTitle(),
                        product.getImageUrl(),
                        product.getLink(),
                        product.getPrice(),
                        product.getBrand() != null
                                ? product.getBrand()
                                : "기타",
                        cardExplanations.getOrDefault(
                                product.getId(),
                                ""
                        )
                ))
                .collect(Collectors.toList());
    }
}
