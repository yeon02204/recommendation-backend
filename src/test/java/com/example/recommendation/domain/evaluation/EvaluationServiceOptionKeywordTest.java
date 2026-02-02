package com.example.recommendation.domain.evaluation;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.Product;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationServiceOptionKeywordTest {

    private final EvaluationService evaluationService = new EvaluationService();

    @Test
    void optionKeyword는_여러번_등장해도_1점만_부여된다() {
        // given
        Product product = new Product(
                1L,
                "무선 무선 무선 헤드셋",
                100000,
                false
        );

        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("무선"),
                        null,
                        "SONY"
                );

        // when
        EvaluationResult result =
                evaluationService.evaluate(List.of(product), criteria);

        EvaluatedProduct evaluated = result.getProducts().get(0);

        // then
        assertThat(evaluated.getScore()).isEqualTo(1);
        assertThat(evaluated.hasKeywordMatch()).isTrue();
        assertThat(result.hasKeywordMatch()).isTrue();
    }
}
