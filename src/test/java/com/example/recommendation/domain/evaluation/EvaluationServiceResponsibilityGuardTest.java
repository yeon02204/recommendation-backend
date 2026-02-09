package com.example.recommendation.domain.evaluation;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.dto.Product;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluationServiceResponsibilityGuardTest {

    private final EvaluationService evaluationService = new EvaluationService();

    @Test
    void evaluationService는_판단이_아닌_사실만_생성한다() {
        // given
        Product product = new Product(
                1L,
                "무선 헤드셋",
                100000,
                true
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

        // then
        assertThat(result).isNotNull();
        assertThat(result.getProducts()).hasSize(1);

        // 판단 결과 같은 건 존재하지 않아야 한다
        assertThat(result.hasKeywordMatch()).isTrue();
        assertThat(result.hasBrandMatch()).isTrue();
    }
}
