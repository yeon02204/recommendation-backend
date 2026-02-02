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

/**
 * [도메인 경계 선언]
 *
 * 이 Product는 "내부 추천 도메인 전용 모델"이다.
 *
 * - 네이버 API 응답 구조를 반영하지 않는다.
 * - 외부 필드 추가 금지
 * - JSON / HTTP / API 개념 금지
 *
 * 네이버 응답 → Product 변환은
 * 반드시 Mapper 계층에서 수행한다.
 *
 * ❌ 이 클래스에서 외부 DTO를 직접 받거나
 * ❌ 외부 응답에 맞춰 필드를 추가하는 행위 금지
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
