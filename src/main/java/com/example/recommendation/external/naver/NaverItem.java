package com.example.recommendation.external.naver;

/**
 * [역할]
 * - 네이버 쇼핑 API item 단위 응답 모델
 *
 * [원칙]
 * - 네이버가 내려주는 "사실 데이터"만 그대로 보관
 * - 해석 / 판단 / 가공 ❌
 */
public class NaverItem {

    private String productId;
    private String title;
    private String brand;

    // ✅ 프론트/링크용 필드
    private String image;   // 상품 이미지 URL
    private String link;    // 상품 상세 링크

    /* =====================
       Getter / Setter
       ===================== */

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
