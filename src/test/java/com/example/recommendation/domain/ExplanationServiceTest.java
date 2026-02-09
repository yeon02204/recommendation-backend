package com.example.recommendation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.external.openai.FakeOpenAiExplanationClient;
import com.example.recommendation.external.openai.OpenAiExplanationClient;

class ExplanationServiceTest {

    private final OpenAiExplanationClient fakeOpenAiClient =
            new FakeOpenAiExplanationClient();

    private final ExplanationService explanationService =
            new ExplanationService(fakeOpenAiClient);
    
    @Test
    void 설명_생성시_OpenAI_Client를_호출한다() {
        // given
        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("가성비"),
                        null,
                        null
                );

        List<EvaluatedProduct> products = List.of();

        // when
        explanationService.generateExplanation(products, criteria);

        // then
        // FakeClient라 verify는 못 쓰니
        // 이 테스트 자체를 제거하거나 이름 변경이 더 낫다
    }

}
