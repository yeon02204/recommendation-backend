package com.example.recommendation.external.naver;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * [역할]
 * - 네이버 API 연동 전 테스트용 더미 구현체
 * - 고정된 Product 후보군만 반환
 */
@Component
@Profile("test") // 테스트에서는 Fake를 쓰게 함 (프로젝트가 다른 프로파일 쓰면 그에 맞춰 변경)
public class FakeNaverClient implements NaverClient {

    @Override
    public List<Product> search(String keyword) {
        return List.of(
            new Product(1L, "무선 헤드셋", "Sony"),
            new Product(2L, "유선 헤드셋", null),
            new Product(3L, "블루투스 이어폰", "Samsung")
        );
    }
}
