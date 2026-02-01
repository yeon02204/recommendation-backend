package com.example.recommendation.domain.evaluation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.Product;

/**
 * [역할]
 * - 검색된 상품 목록을 평가하여
 *   추천 판단에 필요한 "사실 데이터"를 생성한다.
 *
 * [중요]
 * - 판단을 하지 않는다.
 * - RECOMMEND / REQUERY / INVALID 결정 ❌
 * - 오직 EvaluationResult에 들어갈 데이터만 계산
 *
 * [책임]
 * - 점수 계산
 * - 정렬
 * - 상위 N개 선별
 * - 집합 특성 계산 (keyword / brand 매칭 여부)
 */
@Service
public class EvaluationService {

    public EvaluationResult evaluate(
            List<Product> products,
            RecommendationCriteria criteria
    ) {

        // 0️⃣ 검색 결과 없음
        if (products == null || products.isEmpty()) {
            return EvaluationResult.empty();
        }

        // 1️⃣ 점수 계산 + 정렬 + 상위 5개
        List<EvaluatedProduct> evaluatedProducts =
                products.stream()
                        .map(product ->
                                new EvaluatedProduct(
                                        product,
                                        score(product, criteria)
                                )
                        )
                        .sorted(
                                Comparator.comparingInt(
                                        EvaluatedProduct::getScore
                                ).reversed()
                        )
                        .limit(5)
                        .toList();

        // 2️⃣ 집합 특성 계산 (사실 데이터)
        boolean hasBrandMatch =
                evaluatedProducts.stream()
                        .anyMatch(ep ->
                                criteria.isBrandPreferred()
                                && ep.getProduct().hasBrand()
                        );

        // hasKeywordMatch는 현재 점수 기반 임시 판단
        // 추후 optionKeyword 점수 도입 시 대체 예정
        boolean hasKeywordMatch =
                evaluatedProducts.stream()
                        .anyMatch(ep ->
                                ep.getScore() >= 2
                        );

        // 3️⃣ EvaluationResult 생성
        return EvaluationResult.of(
                evaluatedProducts,
                hasKeywordMatch,
                hasBrandMatch
        );
    }

    private int score(
            Product product,
            RecommendationCriteria criteria
    ) {
        int score = 0;

        if (criteria.getPriceRange() != null) {
            score += 1;
        }

        if (criteria.isBrandPreferred() && product.hasBrand()) {
            score += 1;
        }

        return score;
    }
}
