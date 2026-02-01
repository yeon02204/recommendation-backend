package com.example.recommendation.domain.search;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.NaverClient;
import com.example.recommendation.external.naver.Product;

/**
 * [역할]
 * - RecommendationCriteria를
 *   네이버 쇼핑 API 호출 파라미터로 "변환"한다.
 *
 * [중요]
 * - 검색어를 새로 만들지 않는다 ❌
 * - 조건을 추론하지 않는다 ❌
 * - 판단하지 않는다 ❌
 *
 * Criteria에 들어있는 값만 그대로 사용한다.
 */
@Service
public class SearchService {

    private final NaverClient naverClient;

    public SearchService(NaverClient naverClient) {
        this.naverClient = naverClient;
    }

    /**
     * 추천 기준을 기반으로 네이버 쇼핑 검색 수행
     */
    public List<Product> search(RecommendationCriteria criteria) {

        // 1️⃣ Criteria에서 그대로 꺼낸다 (가공 ❌)
        String keyword = criteria.getSearchKeyword();
        Integer maxPrice = criteria.getPriceMax();

        // 2️⃣ 네이버 검색 호출
        return naverClient.search(keyword, maxPrice);
    }
}
