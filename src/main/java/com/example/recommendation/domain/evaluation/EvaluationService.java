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
 * - 집합 특성 계산 (hasBrandMatch / hasKeywordMatch)
 *
 * [현재 MVP 제약]
 * - CriteriaService가 optionKeywords / preferredBrand 문자열을 만들지 않으므로
 *   optionKeyword 매칭 기반 신호는 만들 수 없다.
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

        // hasBrandMatch:
        // - Criteria에서 brandPreferred가 true이고
        // - Product가 브랜드 정보를 가지고 있으면 (hasBrand)
        // - 후보 중 하나라도 해당되면 true
        boolean hasBrandMatch =
                evaluatedProducts.stream()
                        .anyMatch(ep ->
                                criteria.isBrandPreferred()
                                        && ep.getProduct().hasBrand()
                        );

        // hasKeywordMatch:
        // - 현재 CriteriaService는 optionKeywords를 생성하지 않는다.
        // - 따라서 "optionKeyword 매칭"이라는 사실 데이터 자체가 존재할 수 없으므로 항상 false.
        boolean hasKeywordMatch = false;

        // 3️⃣ EvaluationResult 생성
        return EvaluationResult.of(
                evaluatedProducts,
                hasKeywordMatch,
                hasBrandMatch
        );
    }

    /**
     * 점수 계산 (현재 MVP: CriteriaService가 제공하는 사실 데이터만 사용)
     *
     * - priceRange / priceMax가 존재하면 +1
     * - brandPreferred가 true이고 product.hasBrand()면 +1
     */
    private int score(
            Product product,
            RecommendationCriteria criteria
    ) {
        int score = 0;

        // 가격 관련 신호 (CriteriaService가 setPriceRange/setPriceMax로 채움)
        if (criteria.getPriceRange() != null) {
            score += 1;
        }

        // 브랜드 선호 신호 (CriteriaService가 "브랜드" 포함 시 brandPreferred=true)
        if (criteria.isBrandPreferred() && product.hasBrand()) {
            score += 1;
        }

        return score;
    }
}
