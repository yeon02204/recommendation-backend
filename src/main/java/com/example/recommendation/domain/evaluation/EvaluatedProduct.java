package com.example.recommendation.domain.evaluation;

import com.example.recommendation.external.naver.Product;

public class EvaluatedProduct {

    private final Product product;
    private final int score;

    public EvaluatedProduct(Product product, int score) {
        this.product = product;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public Product getProduct() {
        return product;
    }
}
