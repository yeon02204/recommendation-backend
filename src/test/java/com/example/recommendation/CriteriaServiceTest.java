package com.example.recommendation;

import com.example.recommendation.domain.criteria.CriteriaService;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CriteriaServiceTest {

    private final CriteriaService criteriaService =
            new CriteriaService(new FakeOpenAiClient());

    @Test
    void 사용자_입력에서_검색키워드가_그대로_설정된다() {
        // given
        String userInput = "가성비 좋은 헤드셋";

        // when
        RecommendationCriteria criteria =
                criteriaService.createCriteria(userInput);

        // then
        assertThat(criteria.getSearchKeyword())
                .isEqualTo("가성비 좋은 헤드셋");
    }
}