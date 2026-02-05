package com.example.recommendation.domain.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.NaverClient;
import com.example.recommendation.external.naver.Product;

/**
 * [역할]
 * - RecommendationCriteria를 기반으로
 *   네이버 쇼핑 API에 전달할 "검색어(query)"를 구성하고
 *   검색을 수행한다.
 *
 * [책임 경계]
 * - 검색(Search)은 검색이다
 * - 평가는 Evaluation 단계에서 수행한다
 *
 * [중요 원칙]
 * - 검색어를 새로 "창작"하지 않는다 ❌
 * - 조건을 추론하거나 중요도를 판단하지 않는다 ❌
 * - 추천 가능/불가능을 판단하지 않는다 ❌
 *
 * Criteria에 들어있는 값만을 사용해
 * 네이버 검색 품질을 높이기 위한 "query 문자열"만 구성한다.
 */
@Service
public class SearchService {

    private final NaverClient naverClient;

    public SearchService(NaverClient naverClient) {
        this.naverClient = naverClient;
    }

    /**
     * 추천 기준을 기반으로 네이버 쇼핑 검색 수행
     *
     * 처리 흐름:
     * 1. RecommendationCriteria -> query 문자열 생성
     * 2. 네이버 쇼핑 검색 수행
     * 3. 검색 결과 중복 제거 (Search 책임)
     *
     * @param criteria 추천 판단 이전 단계의 조건 데이터
     * @return 네이버 쇼핑 검색 결과 상품 리스트 (중복 제거됨)
     */
    public List<Product> search(RecommendationCriteria criteria) {

        // 1️⃣ Criteria -> query 문자열 생성
        String query = NaverQueryMapper.toQuery(criteria);

        // 2️⃣ 네이버 API 호출
        List<Product> products = naverClient.search(query);

        // 3️⃣ 검색 결과 중복 제거
        return deduplicate(products);
    }

    /**
     * 검색 결과 중복 제거
     *
     * [중복 기준]
     * - id 동일
     * - link 동일
     * - title 완전 동일 (단, <b> 태그 제거 후 비교)
     *
     * ※ 점수/정책/의미와 무관한 "후보 집합 정리" 단계
     * ※ Search 단계 책임
     */
    private List<Product> deduplicate(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return products;
        }

        Map<String, Product> uniqueMap = new LinkedHashMap<>();

        for (Product product : products) {
            String normalizedTitle = normalizeTitle(product.getTitle());

            String key =
                    product.getId() + "|" +
                    product.getLink() + "|" +
                    normalizedTitle;

            // 이미 동일한 key가 있으면 skip
            uniqueMap.putIfAbsent(key, product);
        }

        return new ArrayList<>(uniqueMap.values());
    }

    /**
     * title 정규화
     * - <b> 태그 제거
     * - 공백 trim
     *
     * ※ 의미 해석 아님 / 정책 아님 / 단순 비교용 정규화
     */
    private String normalizeTitle(String title) {
        if (title == null) {
            return "";
        }
        return title
                .replaceAll("<[^>]*>", "")
                .trim();
    }
}
