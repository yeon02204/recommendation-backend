package com.example.recommendation.domain.evaluation;

import com.example.recommendation.external.naver.Product;

public class EvaluatedProduct {

    private final Product product;
    private final int score;

    // ⭐ 사실 데이터
    private final boolean hasBrandMatch;
    private final boolean hasKeywordMatch;

    public EvaluatedProduct(
            Product product,
            int score,
            boolean hasBrandMatch,
            boolean hasKeywordMatch
            
            
    ) {
        this.product = product;
        this.score = score;
        this.hasBrandMatch = hasBrandMatch;
        this.hasKeywordMatch = hasKeywordMatch;
        
        
    }

    public int getScore() {
        return score;
    }

    public Product getProduct() {
        return product;
    }

    public boolean hasKeywordMatch() {
        return hasKeywordMatch;
    }
    
    public boolean hasBrandMatch() {
        return hasBrandMatch;
    }

    
}
