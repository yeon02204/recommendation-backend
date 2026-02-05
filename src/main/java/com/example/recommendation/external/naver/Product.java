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

    // 네이버 원본 데이터 (프론트 전달용)
    private final String imageUrl;
    private final String link;

    // 테스트/기존 코드 호환용
    private final int price;
    private final boolean hasBrand;

    // 브랜드 문자열 (nullable)
    private final String brand;

    /**
     * ✅ 기존 테스트 계약 호환 생성자
     * (Long, String, int, boolean)
     */
    public Product(Long id, String title, int price, boolean hasBrand) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.hasBrand = hasBrand;
        this.brand = hasBrand ? "UNKNOWN" : null;

        // 프론트용 필드는 테스트에서는 없음
        this.imageUrl = null;
        this.link = null;
    }

    /**
     * ✅ RealNaverClient 매핑용 생성자 (기존)
     * (Long, String, String)
     */
    public Product(Long id, String title, String brand) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.hasBrand = brand != null && !brand.isBlank();
        this.price = 0;

        this.imageUrl = null;
        this.link = null;
    }

    /**
     * ✅ RealNaverClient 실제 사용 생성자
     * (네이버 응답 그대로 담기)
     */
    public Product(
            Long id,
            String title,
            String brand,
            String imageUrl,
            String link
    ) {
        this.id = id;
        this.title = title;
        this.brand = brand;
        this.hasBrand = brand != null && !brand.isBlank();
        this.price = 0;

        this.imageUrl = imageUrl;
        this.link = link;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    // 테스트/기존 코드 호환용
    public int getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public boolean hasBrand() {
        return hasBrand;
    }

    // ✅ 프론트 전달용
    public String getImageUrl() {
        return imageUrl;
    }

    public String getLink() {
        return link;
    }
}
