package com.example.recommendation.domain.decision;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

class DecisionAmbiguousPolicyTest {

    private final DecisionMaker decisionMaker = new DecisionMaker();

    @Test
    void 상위_점수_차이가_1이하면_REQUERY다() {
        // given
        EvaluationResult result =
                EvaluationResult.testOf(
                        2,      // candidateCount
                        2,      // topScore
                        1,      // secondScore
                        true,   // hasKeywordMatch
                        false   // hasBrandMatch
                );

        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        List.of("무선"),
                        null,
                        null
                );

        // when
        Decision decision = decisionMaker.decide(result, criteria);

        // then
        assertThat(decision.getType())
                .isEqualTo(DecisionType.REQUERY);
    }
}
