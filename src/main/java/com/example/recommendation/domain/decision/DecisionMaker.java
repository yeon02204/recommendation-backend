package com.example.recommendation.domain.decision;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import org.springframework.stereotype.Component;

/**
 * [역할]
 * - EvaluationResult라는 "사실 데이터"를 해석하여
 *   추천 가능 여부를 판단한다.
 *
 * [원칙]
 * - 상품 선택 ❌
 * - 점수 계산 ❌
 * - AI 호출 ❌
 * - 오직 상태 판단만 수행
 */
@Component
public class DecisionMaker {

    public Decision decide(
            EvaluationResult result,
            RecommendationCriteria criteria
    ) {

        // 1️⃣ 추천 불가: 후보 상품 없음
        if (result.getCandidateCount() == 0) {
            return Decision.invalid(
                    "추천 가능한 상품이 없습니다."
            );
        }

        // 2️⃣ 후보가 1개뿐인 경우
        // 비교 대상이 없으므로 ambiguous 판단 생략
        if (result.getCandidateCount() == 1) {
            return Decision.recommend(
                    "조건에 맞는 최선의 상품을 추천합니다."
            );
        }

        // 3️⃣ 추천 근거 신호 부족
        boolean hasNoSignal =
                !result.hasKeywordMatch()
                && !result.hasBrandMatch();

        if (hasNoSignal) {
            return Decision.requery(
                    "추천 근거가 부족합니다.",
                    "조금 더 구체적인 조건을 알려주실 수 있을까요?"
            );
        }

        // 4️⃣ 상위 후보 간 점수 차이가 작아 애매한 경우
        boolean ambiguousTop =
                result.getTopScore() - result.getSecondScore() <= 1;

        if (ambiguousTop) {
            return Decision.requery(
                    "후보 상품 간 차이가 명확하지 않습니다.",
                    "조금 더 구체적인 조건을 알려주실 수 있을까요?"
            );
        }

        // 5️⃣ 명확한 추천 가능
        return Decision.recommend(
                "충분한 근거로 추천 가능합니다."
        );
    }
}
