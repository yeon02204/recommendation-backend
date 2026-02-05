package com.example.recommendation.domain.evaluation;

import java.util.List;
import java.util.Set;

import com.example.recommendation.external.naver.Product;

public class EvaluatedProduct {

    private final Product product;
    private final int score;

    // 기존 사실
    private final boolean hasBrandMatch;
    private final boolean hasKeywordMatch;

    // ⭐ 4단계 확장 사실
    private final List<String> matchedOptionKeywords;
    private final Set<MatchField> matchedOptionFields;

    public EvaluatedProduct(
            Product product,
            int score,
            boolean hasBrandMatch,
            boolean hasKeywordMatch,
            List<String> matchedOptionKeywords,
            Set<MatchField> matchedOptionFields
    ) {
        this.product = product;
        this.score = score;
        this.hasBrandMatch = hasBrandMatch;
        this.hasKeywordMatch = hasKeywordMatch;
        this.matchedOptionKeywords = matchedOptionKeywords;
        this.matchedOptionFields = matchedOptionFields;
    }

    public Product getProduct() {
        return product;
    }

    public int getScore() {
        return score;
    }

    public boolean hasBrandMatch() {
        return hasBrandMatch;
    }

    public boolean hasKeywordMatch() {
        return hasKeywordMatch;
    }

    public List<String> getMatchedOptionKeywords() {
        return matchedOptionKeywords;
    }

    public Set<MatchField> getMatchedOptionFields() {
        return matchedOptionFields;
    }
}
