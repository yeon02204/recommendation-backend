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
    void 설명_문장은_OpenAI_Client_결과를_그대로_반환한다() {
        // given
        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("가성비"),
                        null,
                        null
                );

        List<EvaluatedProduct> products = List.of(); // 내용 중요 ❌

        // when
        String explanation =
                explanationService.generateExplanation(products, criteria);

        // then
        assertThat(explanation)
                .isEqualTo("테스트용 설명 문장입니다.");
    }
}
