package com.example.recommendation.domain.evaluation;

import java.util.List;

/**
 * [역할]
 * - EvaluationService의 평가 결과를 담는 객체
 * - "사실 데이터"만 포함한다
 *
 * [중요]
 * - 판단 로직 포함 금지
 * - isValid / isInvalid 같은 메서드 금지
 */
public class EvaluationResult {

    private final List<EvaluatedProduct> products;
    private final int candidateCount;
    private final int topScore;
    private final int secondScore;
    private final boolean hasKeywordMatch;
    private final boolean hasBrandMatch;

    private EvaluationResult(
            List<EvaluatedProduct> products,
            int candidateCount,
            int topScore,
            int secondScore,
            boolean hasKeywordMatch,
            boolean hasBrandMatch
    ) {
        this.products = products;
        this.candidateCount = candidateCount;
        this.topScore = topScore;
        this.secondScore = secondScore;
        this.hasKeywordMatch = hasKeywordMatch;
        this.hasBrandMatch = hasBrandMatch;
    }

    // 후보 없음
    public static EvaluationResult empty() {
        return new EvaluationResult(
                List.of(), 0, 0, 0, false, false
        );
    }

    // 정상 평가 결과
    public static EvaluationResult of(
            List<EvaluatedProduct> products,
            boolean hasKeywordMatch,
            boolean hasBrandMatch
    ) {
        int count = products.size();
        int top = count > 0 ? products.get(0).getScore() : 0;
        int second = count > 1 ? products.get(1).getScore() : 0;

        return new EvaluationResult(
                products,
                count,
                top,
                second,
                hasKeywordMatch,
                hasBrandMatch
        );
    }

    /* ========= getters ========= */

    public List<EvaluatedProduct> getProducts() {
        return products;
    }

    public int getCandidateCount() {
        return candidateCount;
    }

    public int getTopScore() {
        return topScore;
    }

    public int getSecondScore() {
        return secondScore;
    }

    public boolean hasKeywordMatch() {
        return hasKeywordMatch;
    }

    public boolean hasBrandMatch() {
        return hasBrandMatch;
    }
}
