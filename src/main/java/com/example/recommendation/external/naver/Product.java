package com.example.recommendation.external.naver;

/**
 * [역할]
 * - 네이버 쇼핑 API 상품 표현
 * - MVP에서는 더미 데이터용
 *
 * [중요]
 * - 비즈니스 로직 없음
 * - 판단 없음
 */
public class Product {

    private final Long id;
    private final String title;
    private final int price;
    private final boolean hasBrand;

    public Product(Long id, String title, int price, boolean hasBrand) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.hasBrand = hasBrand;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getPrice() {
        return price;
    }

    public boolean hasBrand() {
        return hasBrand;
    }
}
