package com.example.recommendation.domain.evaluation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.Product;

/**
 * [정책 테스트]
 *
 * 목적:
 * - optionKeywordMatch = +1
 * - brandMatch = +1
 * - 가중치는 MVP 정책이며 변경되면 안 된다
 *
 * 이 테스트는 "추천 품질"이 아니라
 * "점수 정책 고정"을 검증한다.
 */
class EvaluationPolicyTest {

    @Test
    void optionKeyword와_brandMatch는_각각_1점이다() {
        // given
    	Product product = new Product(
    		    1L,
    		    "무선 게이밍 헤드셋",
    		    10000,
    		    true   // hasBrand
    		);


        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("무선"),   // optionKeyword 1개
                        null,
                        "로지텍"           // preferredBrand
                );

        EvaluationService evaluationService = new EvaluationService();

        // when
        EvaluationResult result =
                evaluationService.evaluate(List.of(product), criteria);

        // then
        assertThat(result.getCandidateCount()).isEqualTo(1);
        assertThat(result.getTopScore()).isEqualTo(2);
        assertThat(result.hasKeywordMatch()).isTrue();
        assertThat(result.hasBrandMatch()).isTrue();
    }
}
