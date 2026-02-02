package com.example.recommendation.domain.decision;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.evaluation.EvaluationService;
import com.example.recommendation.external.naver.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationDecisionIntegrationTest {

    @Test
    void empty_products_flow_evaluation_to_decision() {
        // given: 검색 결과 0개(=상품 없음) + 빈 criteria
        List<Product> products = List.of();
        RecommendationCriteria criteria = new RecommendationCriteria();

        EvaluationService evaluationService = new EvaluationService();
        DecisionMaker decisionMaker = new DecisionMaker();

        // when: Evaluation 결과를 Decision이 그대로 소비
        EvaluationResult result = evaluationService.evaluate(products, criteria);
        Decision decision = decisionMaker.decide(result, criteria);

        // then: 후보 0개면 INVALID
        assertNotNull(result);
        assertNotNull(decision);
        assertEquals(DecisionType.INVALID, decision.getType());
    }
}