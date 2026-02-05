package com.example.recommendation.domain.recommendation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.decision.Decision;
import com.example.recommendation.domain.decision.DecisionMaker;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
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

    public RecommendationResponseDto recommend(RecommendationCriteria criteria) {

        // 1️⃣ Search
        List<Product> products = searchService.search(criteria);

        // 2️⃣ Evaluation
        EvaluationResult result = evaluationService.evaluate(products, criteria);

        // 3️⃣ Decision
        Decision decision = decisionMaker.decide(result, criteria);

        // 4️⃣ Response
        switch (decision.getType()) {

            case INVALID:
                return RecommendationResponseDto.invalid(decision.getReason());

            case REQUERY:
                return RecommendationResponseDto.requery(
                        explanationService.generateByPolicy(
                                decision.getExplanationPolicy()
                        )
                );

            case RECOMMEND:
                // 상단 설명 문장 (공통)
                String explanation =
                        explanationService.generateExplanation(
                                result.getProducts(),
                                criteria
                        );

                // 카드용 아이템 (네이버 데이터 그대로, 표현 계층 확장)
                List<Item> items =
                        result.getProducts().stream()
                                .map(EvaluatedProduct::getProduct)
                                .map(p -> new Item(
                                        p.getId(),          // productId
                                        p.getTitle(),       // title
                                        p.getImageUrl(),    // imageUrl
                                        p.getLink(),        // link
                                        p.getPrice(),       // price (기존 호환)
                                        null,               // mallName (아직 Product에 없음)
                                        explanation         // 공통 설명 문장
                                ))
                                .collect(Collectors.toList());

                return RecommendationResponseDto.recommend(items, explanation);

            default:
                throw new IllegalStateException(
                        "Unhandled decision type: " + decision.getType()
                );
        }
    }
}
