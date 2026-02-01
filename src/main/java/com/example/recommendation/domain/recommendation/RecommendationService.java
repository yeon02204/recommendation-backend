package com.example.recommendation.domain.recommendation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.decision.Decision;
import com.example.recommendation.domain.decision.DecisionMaker;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.evaluation.EvaluationService;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.domain.search.SearchService;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.dto.RecommendationResponseDto.Item;
import com.example.recommendation.external.naver.Product;

@Service
public class RecommendationService {

    private final SearchService searchService;
    private final EvaluationService evaluationService;
    private final DecisionMaker decisionMaker;
    private final ExplanationService explanationService;

    public RecommendationService(
            SearchService searchService,
            EvaluationService evaluationService,
            DecisionMaker decisionMaker,
            ExplanationService explanationService
    ) {
        this.searchService = searchService;
        this.evaluationService = evaluationService;
        this.decisionMaker = decisionMaker;
        this.explanationService = explanationService;
    }

    public RecommendationResponseDto recommend(
            RecommendationCriteria criteria
    ) {

        // 1️⃣ Search
        List<Product> products =
                searchService.search(criteria);

        // 2️⃣ Evaluation
        EvaluationResult result =
                evaluationService.evaluate(products, criteria);

        // 3️⃣ Decision
        Decision decision =
                decisionMaker.decide(result, criteria);

        // 4️⃣ Response 조립
        switch (decision.getType()) {

            case INVALID:
                return RecommendationResponseDto.invalid(
                        decision.getReason()
                );

            case REQUERY:
                return RecommendationResponseDto.requery(
                        decision.getFollowUpQuestion()
                );

            case RECOMMEND:
                // 4-1. 설명 문장 생성 (AI 호출은 여기서만)
                String explanation =
                        explanationService.generateExplanation(
                                result.getProducts(),
                                criteria
                        );

                // 4-2. Domain → Response Item 변환
                // ⚠️ MVP 단계에서는 productId를 사용하지 않는다
                List<Item> items =
                        result.getProducts().stream()
                                .map(p ->
                                        new Item(
                                                null, // productId는 외부 계약 확정 후 채움
                                                explanation
                                        )
                                )
                                .collect(Collectors.toList());

                // 4-3. 외부 계약 DTO 반환
                return RecommendationResponseDto.recommend(
                        items,
                        explanation
                );

            default:
                throw new IllegalStateException(
                        "Unhandled decision type: " + decision.getType()
                );
        }
    }
}
