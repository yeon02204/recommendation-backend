package com.example.recommendation.domain.decision;

import com.example.recommendation.domain.criteria.ConversationContext;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.evaluation.EvaluationService;
import com.example.recommendation.external.naver.dto.Product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationDecisionIntegrationTest {

    @Test
    void empty_products_flow_evaluation_to_decision() {
        // given: 검색 결과 0개 + 빈 criteria
        List<Product> products = List.of();

        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        null,
                        List.of(),
                        null,
                        null
                );

        ConversationContext context = new ConversationContext();
        EvaluationService evaluationService = new EvaluationService();
        DecisionMaker decisionMaker = new DecisionMaker();

        // when
        EvaluationResult result =
                evaluationService.evaluate(products, criteria);

        DecisionResult decisionResult =
                decisionMaker.decide(context, criteria, result);

        // then
        assertNotNull(result);
        assertNotNull(decisionResult);
        assertEquals(
                DecisionType.INVALID,
                decisionResult.getDecision().getType()
        );
    }
}
