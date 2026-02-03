package com.example.recommendation.external.naver;

/**
 * [역할]
 * - 네이버 쇼핑 API 상품 표현 (외부 연동 모델)
 *
 * [중요]
 * - 도메인 판단 로직 없음
 * - 테스트 계약(기존 생성자 시그니처) 호환 유지
 */
public class Product {

    private final Long id;
    private final String title;

    // 테스트/기존 코드 호환용 (현재 MVP에서는 사용하지 않더라도 남겨둠)
    private final int price;
    private final boolean hasBrand;

    // RealNaverClient 등에서 사용할 수 있도록 문자열 brand도 보관 (nullable)
    private final String brand;

    /**
     * ✅ 기존 테스트 계약 호환 생성자
     * (long, String, int, boolean)
     */
    public Product(Long id, String title, int price, boolean hasBrand) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.hasBrand = hasBrand;
        // boolean만 주어진 경우 brand는 의미 해석하지 않고 최소 표현으로만 둔다
        this.brand = hasBrand ? "UNKNOWN" : null;
    }

    /**
     * ✅ RealNaverClient 매핑용 생성자
     * (Long, String, String)
     */
    public Product(Long id, String title, String brand) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.hasBrand = brand != null && !brand.isBlank();
        this.price = 0; // MVP에서 가격은 사용하지 않음 (호환 필드만 유지)
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // 기존 테스트/코드 호환용
    public int getPrice() {
        return price;
    }

    // RealNaverClient에서 들어온 brand 확인이 필요하면 사용 가능
    public String getBrand() {
        return brand;
    }

    // 정책: 브랜드 "존재 여부"만 의미 있음
    public boolean hasBrand() {
        return hasBrand;
    }
}
