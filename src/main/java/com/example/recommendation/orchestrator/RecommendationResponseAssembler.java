package com.example.recommendation.orchestrator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.explanation.CardExplanationPrompt;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.dto.RecommendationResponseDto;

@Component
public class RecommendationResponseAssembler {

    private static final Logger log =
            LoggerFactory.getLogger(RecommendationResponseAssembler.class);

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
        log.info("[Assembler] buildMainMessage start");

        if (evaluationResult.getCandidateCount() == 0) {
            log.info("[Assembler] candidateCount=0 → REQUERY 메시지 생성");

            String msg =
                    explanationService.generateByPolicy(
                            com.example.recommendation.domain.explanation.ExplanationPolicy
                                    .REQUERY_NEED_MORE_CONDITION
                    );

            log.info("[Assembler] mainMessage='{}'", msg);
            return msg;
        }

        String msg =
                explanationService.generateExplanation(
                        evaluationResult.getProducts(),
                        criteria
                );

        log.info(
            "[Assembler] mainMessage generated (length={})",
            msg == null ? 0 : msg.length()
        );

        return msg;
    }

    /* =========================
     * 카드별 설명
     * ========================= */
    public Map<Long, String> buildCardExplanations(
            EvaluationResult evaluationResult,
            RecommendationCriteria criteria
    ) {
        log.info("[Assembler] buildCardExplanations start");

        if (evaluationResult.getCandidateCount() == 0) {
            log.info("[Assembler] candidateCount=0 → 카드 설명 없음");
            return Map.of();
        }

        List<CardExplanationPrompt> prompts =
                evaluationResult.getProducts().stream()
                        .map(this::toPrompt)
                        .toList();

        log.info("[Assembler] cardPromptCount={}", prompts.size());

        Map<Long, String> explanations =
                explanationService.generateCardExplanations(
                        prompts,
                        criteria
                );

        log.info(
            "[Assembler] cardExplanationCount={}",
            explanations == null ? 0 : explanations.size()
        );

        return explanations;
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
     * Item 조립 (🔥 핵심 수정)
     * ========================= */
    public List<RecommendationResponseDto.Item> assembleItems(
            EvaluationResult evaluationResult,
            Map<Long, String> cardExplanations
    ) {
        log.info("[Assembler] assembleItems start");

        if (evaluationResult.getCandidateCount() == 0) {
            log.info("[Assembler] candidateCount=0 → items=[]");
            return List.of();
        }

        List<EvaluatedProduct> evaluatedProducts =
                evaluationResult.getProducts();

        log.info(
            "[Assembler] evaluatedProductCount={}, cardExplanationKeys={}",
            evaluatedProducts.size(),
            cardExplanations == null ? 0 : cardExplanations.keySet()
        );

        List<RecommendationResponseDto.Item> items =
                evaluatedProducts.stream()
                        .map(p -> {
                            Long productId = p.getProduct().getId();
                            String explanation =
                                    cardExplanations.getOrDefault(
                                            productId,
                                            ""
                                    );

                            log.debug(
                                "[Assembler] item id={} explanation='{}'",
                                productId,
                                explanation
                            );

                            return new RecommendationResponseDto.Item(
                                    productId,
                                    p.getProduct().getTitle(),
                                    p.getProduct().getImageUrl(),
                                    p.getProduct().getLink(),
                                    p.getProduct().getPrice(),        // 🔥 이제 실제 가격
                                    p.getProduct().getMallName() != null
                                            ? p.getProduct().getMallName()
                                            : "기타",                  // 🔥 mallName 사용
                                    explanation
                            );
                        })
                        .collect(Collectors.toList());

        log.info("[Assembler] assembledItemCount={}", items.size());
        return items;
    }
}