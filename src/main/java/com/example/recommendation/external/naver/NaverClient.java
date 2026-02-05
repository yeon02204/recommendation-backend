package com.example.recommendation.external.naver;

import java.util.List;

/**
 * 네이버 쇼핑 API 호출을 추상화한 외부 연동 인터페이스
 *
 * [역할]
 * - 이미 결정된 검색어(query)를 받아
 *   네이버 쇼핑 검색을 수행한다.
 *
 * [중요]
 * - 도메인 객체(RecommendationCriteria)를 알지 않는다 ❌
 * - 검색어를 조합하거나 해석하지 않는다 ❌
 * - 판단 로직을 포함하지 않는다 ❌
 */
public interface NaverClient {

    /**
     * 네이버 쇼핑 검색 수행
     *
     * @param keyword 네이버 검색에 전달할 최종 검색어(query)
     * @return 검색 결과 상품 리스트
     */
    List<Product> search(String keyword);
}
