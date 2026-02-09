package com.example.recommendation.domain.evaluation;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.dto.Product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationServiceScoreTest {

    private final EvaluationService evaluationService = new EvaluationService();

    @Test
    void optionKeyword와_brandMatch가_모두_있으면_2점이다() {
        // given
        Product product = new Product(
                1L,
                "무선 헤드셋",
                150000,
                true
        );

        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("무선"),
                        null,
                        "SONY" // 브랜드 선호 명시
                );

        // when
        EvaluationResult result =
                evaluationService.evaluate(List.of(product), criteria);

        EvaluatedProduct evaluated = result.getProducts().get(0);

        // then
        assertThat(evaluated.getScore()).isEqualTo(2);
        assertThat(result.hasKeywordMatch()).isTrue();
        assertThat(result.hasBrandMatch()).isTrue();
    }
}
