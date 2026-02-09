package com.example.recommendation.domain.assembler;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.evaluation.MatchField;
import com.example.recommendation.domain.explanation.CardExplanationPrompt;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.domain.recommendation.RecommendationAssembler;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.external.naver.dto.Product;

/**
 * RecommendationAssemblerTest
 *
 * 목적:
 * - EvaluatedProduct → RecommendationResponseDto.Item 조립 검증
 * - 카드별 explanation이 정상 주입되는지 확인
 */
public class RecommendationResponseAssemblerTest {

    @Test
    void 카드별_explanation이_items에_정상_주입된다() {
        // given
        Product product1 =
                new Product(1L, "무선 헤드셋", 10000, true);

        Product product2 =
                new Product(2L, "블루투스 헤드셋", 12000, false);

        EvaluatedProduct ep1 =
                new EvaluatedProduct(
                        product1,
                        1,
                        true,
                        true,
                        List.of("무선"),
                        Set.of(MatchField.TITLE)
                );

        EvaluatedProduct ep2 =
                new EvaluatedProduct(
                        product2,
                        1,
                        false,
                        true,
                        List.of("블루투스"),
                        Set.of(MatchField.TITLE)
                );

        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("무선"),
                        null,
                        null
                );

        FakeExplanationService explanationService =
                new FakeExplanationService();

        RecommendationAssembler assembler =
                new RecommendationAssembler(explanationService);

        // when
        List<RecommendationResponseDto.Item> items =
                assembler.assembleItems(
                        List.of(ep1, ep2),
                        criteria
                );

        // then
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getExplanation())
                .isEqualTo("무선 옵션이 잘 맞는 상품이에요");
        assertThat(items.get(1).getExplanation())
                .isEqualTo("무선 옵션이 잘 맞는 상품이에요");
    }

    /* =========================
     * Fake Explanation Service
     * ========================= */

    static class FakeExplanationService extends ExplanationService {

        public FakeExplanationService() {
            super(null);
        }

        @Override
        public Map<Long, String> generateCardExplanations(
                List<CardExplanationPrompt> prompts,
                RecommendationCriteria criteria
        ) {
            return prompts.stream()
                    .collect(
                            java.util.stream.Collectors.toMap(
                                    CardExplanationPrompt::productId,
                                    p -> "무선 옵션이 잘 맞는 상품이에요"
                            )
                    );
        }
    }
}
