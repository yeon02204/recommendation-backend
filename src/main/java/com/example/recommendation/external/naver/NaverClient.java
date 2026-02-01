package com.example.recommendation.external.naver;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * [역할]
 * - 네이버 쇼핑 API 호출 전용
 *
 * [MVP 더미 구현]
 * - 고정 상품 3개 반환
 * - 실제 API 연동 전까지 테스트용
 */
@Component
public class NaverClient {

    public List<Product> search(String keyword, Integer maxPrice) {

        return List.of(
                new Product(
                        1L,
                        "무선 블루투스 헤드셋",
                        89000,
                        true
                ),
                new Product(
                        2L,
                        "가성비 유선 헤드셋",
                        39000,
                        false
                ),
                new Product(
                        3L,
                        "프리미엄 게이밍 헤드셋",
                        159000,
                        true
                )
        );
    }
}
