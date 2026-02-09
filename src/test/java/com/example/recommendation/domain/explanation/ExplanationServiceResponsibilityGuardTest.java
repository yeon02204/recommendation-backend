package com.example.recommendation.domain.explanation;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.external.naver.dto.Product;
import com.example.recommendation.external.openai.FakeOpenAiExplanationClient;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExplanationServiceResponsibilityGuardTest {

    @Test
    void explanationService는_결정이나_점수를_참조하지_않는다() {
        // given
        ExplanationService explanationService =
                new ExplanationService(new FakeOpenAiExplanationClient());

        EvaluatedProduct product =
                new EvaluatedProduct(
                        new Product(1L, "무선 헤드셋", 100000, true),
                        2,
                        true,
                        true,
                        List.of("무선"),
                        Set.of()
                );

        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("무선"),
                        null,
                        null
                );

        // when
        String explanation =
                explanationService.generateExplanation(
                        List.of(product),
                        criteria
                );

        // then
        assertThat(explanation).isNotNull();
    }
}
