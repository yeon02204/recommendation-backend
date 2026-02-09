package com.example.recommendation.domain.recommendation;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.evaluation.EvaluationService;
import com.example.recommendation.external.naver.Product;

/**
 * ⚠️ IMPORTANT
 *
 * RecommendationService는 "검색 이후(post-search)" 단계 전용이다.
 *
 * [책임]
 * - 검색 결과(Product)를 평가(Evaluation)한다.
 *
 * [금지]
 * ❌ 검색 전 판단
 * ❌ DecisionMaker 호출
 * ❌ 상태 전이 판단
 *
 * 검색 전 판단(HOME / SEARCH 분기)은
 * Orchestrator / SearchReadiness 단계에서 이미 완료되어야 한다.
 */
@Service
public class RecommendationService {

    private static final Logger log =
            LoggerFactory.getLogger(RecommendationService.class);

    private final EvaluationService evaluationService;

    public RecommendationService(
            EvaluationService evaluationService
    ) {
        this.evaluationService = evaluationService;
    }

    /**
     * 검색 결과를 받아 Evaluation만 수행한다.
     *
     * @param criteria 검색 조건
     * @param products 검색된 상품 목록 (이미 검색 완료 상태)
     * @return EvaluationResult (사실 데이터)
     */
    public EvaluationResult evaluate(
            RecommendationCriteria criteria,
            List<Product> products
    ) {
        log.info("[RecommendationService] evaluate start");
        log.info("[RecommendationService] productsCount={}",
                products == null ? 0 : products.size());

        EvaluationResult evaluationResult =
                evaluationService.evaluate(products, criteria);

        log.info("[RecommendationService] evaluation done candidateCount={}",
                evaluationResult.getCandidateCount());

        return evaluationResult;
    }
}
