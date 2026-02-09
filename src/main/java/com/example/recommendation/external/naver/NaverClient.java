package com.example.recommendation.external.naver;

import java.util.List;

import com.example.recommendation.external.naver.dto.Product;

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
 * 
 * 🔥 2025-02-09 업데이트:
 * - search(keyword, start) 오버로드 추가
 * - RETRY_SEARCH 지원
 */
public interface NaverClient {

    /**
     * 네이버 쇼핑 검색 수행 (기본)
     *
     * @param keyword 네이버 검색에 전달할 최종 검색어(query)
     * @return 검색 결과 상품 리스트
     */
    List<Product> search(String keyword);

    /**
     * 🔥 네이버 쇼핑 검색 수행 (start 지정)
     * 
     * @param keyword 네이버 검색에 전달할 최종 검색어(query)
     * @param start 검색 시작 위치 (1~1000)
     *              1: 1~display번째 결과
     *              11: 11~(10+display)번째 결과
     *              21: 21~(20+display)번째 결과
     * @return 검색 결과 상품 리스트
     * 
     * 사용 예:
     * - search("노트북", 1) → 1~30번째 결과
     * - search("노트북", 6) → 6~35번째 결과
     * - search("노트북", 11) → 11~40번째 결과
     */
    List<Product> search(String keyword, int start);
}