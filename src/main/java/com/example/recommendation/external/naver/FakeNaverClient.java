package com.example.recommendation.external.naver;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * [역할]
 * - 네이버 API 연동 전 테스트용 더미 구현체
 * - 고정된 Product 후보군만 반환
 * 
 * 🔥 2025-02-09 업데이트:
 * - start 파라미터 지원 (offset 시뮬레이션)
 */
@Component
@Profile("test")
public class FakeNaverClient implements NaverClient {

    // 🔥 더미 데이터 풀 (30개)
    private static final List<Product> DUMMY_PRODUCTS = List.of(
        // 1~10번째
        new Product(1L, "삼성 가벼운 노트북", "Samsung", "http://image1.jpg", "http://link1"),
        new Product(2L, "LG 가벼운 노트북", "LG", "http://image2.jpg", "http://link2"),
        new Product(3L, "삼성 게이밍 노트북", "Samsung", "http://image3.jpg", "http://link3"),
        new Product(4L, "LG 업무용 노트북", "LG", "http://image4.jpg", "http://link4"),
        new Product(5L, "삼성 학생용 노트북", "Samsung", "http://image5.jpg", "http://link5"),
        new Product(6L, "LG 프로그래밍 노트북", "LG", "http://image6.jpg", "http://link6"),
        new Product(7L, "삼성 디자인 노트북", "Samsung", "http://image7.jpg", "http://link7"),
        new Product(8L, "LG 휴대용 노트북", "LG", "http://image8.jpg", "http://link8"),
        new Product(9L, "삼성 고성능 노트북", "Samsung", "http://image9.jpg", "http://link9"),
        new Product(10L, "LG 경량 노트북", "LG", "http://image10.jpg", "http://link10"),
        
        // 11~20번째
        new Product(11L, "삼성 비즈니스 노트북", "Samsung", "http://image11.jpg", "http://link11"),
        new Product(12L, "LG 울트라북", "LG", "http://image12.jpg", "http://link12"),
        new Product(13L, "삼성 프리미엄 노트북", "Samsung", "http://image13.jpg", "http://link13"),
        new Product(14L, "LG 보급형 노트북", "LG", "http://image14.jpg", "http://link14"),
        new Product(15L, "삼성 멀티미디어 노트북", "Samsung", "http://image15.jpg", "http://link15"),
        new Product(16L, "LG 개발자용 노트북", "LG", "http://image16.jpg", "http://link16"),
        new Product(17L, "삼성 크리에이터 노트북", "Samsung", "http://image17.jpg", "http://link17"),
        new Product(18L, "LG 사무용 노트북", "LG", "http://image18.jpg", "http://link18"),
        new Product(19L, "삼성 하이엔드 노트북", "Samsung", "http://image19.jpg", "http://link19"),
        new Product(20L, "LG 미들급 노트북", "LG", "http://image20.jpg", "http://link20"),
        
        // 21~30번째
        new Product(21L, "삼성 초경량 노트북", "Samsung", "http://image21.jpg", "http://link21"),
        new Product(22L, "LG 스탠다드 노트북", "LG", "http://image22.jpg", "http://link22"),
        new Product(23L, "삼성 2in1 노트북", "Samsung", "http://image23.jpg", "http://link23"),
        new Product(24L, "LG 컨버터블 노트북", "LG", "http://image24.jpg", "http://link24"),
        new Product(25L, "삼성 터치스크린 노트북", "Samsung", "http://image25.jpg", "http://link25"),
        new Product(26L, "LG OLED 노트북", "LG", "http://image26.jpg", "http://link26"),
        new Product(27L, "삼성 롱배터리 노트북", "Samsung", "http://image27.jpg", "http://link27"),
        new Product(28L, "LG 슬림형 노트북", "LG", "http://image28.jpg", "http://link28"),
        new Product(29L, "삼성 워크스테이션 노트북", "Samsung", "http://image29.jpg", "http://link29"),
        new Product(30L, "LG 올인원 노트북", "LG", "http://image30.jpg", "http://link30")
    );

    @Override
    public List<Product> search(String keyword) {
        return search(keyword, 1);
    }

    @Override
    public List<Product> search(String keyword, int start) {
        
        // 🔥 start 기반 offset 시뮬레이션
        // start=1 → index 0부터 (1~30번째)
        // start=6 → index 5부터 (6~35번째, 하지만 30개까지만 있음)
        // start=11 → index 10부터 (11~40번째, 하지만 30개까지만 있음)
        
        int startIndex = start - 1; // start는 1부터 시작
        
        if (startIndex < 0) {
            startIndex = 0;
        }
        
        if (startIndex >= DUMMY_PRODUCTS.size()) {
            return List.of(); // 범위 초과
        }
        
        // 최대 30개까지 반환 (실제 API display 파라미터와 동일)
        int endIndex = Math.min(startIndex + 30, DUMMY_PRODUCTS.size());
        
        List<Product> result = new ArrayList<>(
            DUMMY_PRODUCTS.subList(startIndex, endIndex)
        );
        
        return result;
    }
}