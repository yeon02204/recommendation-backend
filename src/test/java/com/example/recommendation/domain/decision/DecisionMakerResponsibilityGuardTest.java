package com.example.recommendation.domain.decision;

import com.example.recommendation.domain.criteria.ConversationContext;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DecisionMakerResponsibilityGuardTest {

    private final DecisionMaker decisionMaker = new DecisionMaker();

    @Test
    void decisionMaker는_products없이도_판단만_수행할_수_있다() {
        // given
        EvaluationResult result =
                EvaluationResult.testOf(
                        3,      // candidateCount
                        2,      // topScore
                        1,      // secondScore
                        true,   // hasKeywordMatch
                        false   // hasBrandMatch
                );

        RecommendationCriteria criteria =
                new RecommendationCriteria(
                        "헤드셋",
                        null,
                        null,
                        null
                );

        ConversationContext context = new ConversationContext();

        // when
        DecisionResult decisionResult =
                decisionMaker.decide(context, criteria, result);

        // then
        assertThat(decisionResult).isNotNull();
        assertThat(decisionResult.getDecision().getType())
                .isEqualTo(DecisionType.RECOMMEND);
    }
}
