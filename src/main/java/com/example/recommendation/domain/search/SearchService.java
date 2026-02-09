package com.example.recommendation.domain.search;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.NaverClient;
import com.example.recommendation.external.naver.Product;

/**
 * [역할]
 * - 외부 검색 API 호출 전용 서비스
 *
 * [중요 규칙]
 * - 판단 로직 ❌
 * - 재시도 판단 ❌
 * - 조건 변경 ❌
 *
 * 이 서비스는
 * "이미 결정된 조건"을 그대로 외부에 전달하는
 * 순수 I/O 계층이다.
 * 
 * 🔥 2025-02-09 업데이트:
 * - searchWithOffset 추가 (RETRY_SEARCH 지원)
 */

@Service
public class SearchService {

    private static final Logger log =
            LoggerFactory.getLogger(SearchService.class);

    private final NaverClient naverClient;

    public SearchService(NaverClient naverClient) {
        this.naverClient = naverClient;
    }

    /**
     * 기본 검색 (offset 없음)
     */
    public List<Product> search(RecommendationCriteria criteria) {
        return searchWithOffset(criteria, 0);
    }

    /**
     * 🔥 offset 기반 검색 (RETRY_SEARCH 지원)
     * 
     * @param criteria 검색 조건
     * @param offset 건너뛸 상품 개수 (예: 5, 10, 15...)
     * @return 검색 결과 상품 리스트
     * 
     * 사용 예:
     * - offset=0: 1~10번째 결과
     * - offset=5: 6~15번째 결과
     * - offset=10: 11~20번째 결과
     */
    public List<Product> searchWithOffset(
            RecommendationCriteria criteria,
            int offset
    ) {

        log.info("[SearchService] search start (offset={})", offset);

        // 1️⃣ Criteria → query 문자열
        String baseQuery = NaverQueryMapper.toQuery(criteria);

        String finalQuery;
        if (criteria.getPreferredBrand() != null
                && !criteria.getPreferredBrand().isBlank()) {
            finalQuery = criteria.getPreferredBrand() + " " + baseQuery;
        } else {
            finalQuery = baseQuery;
        }

        // 🔥 offset → start 변환
        // offset=0 → start=1 (1~30번째)
        // offset=5 → start=6 (6~35번째)
        // offset=10 → start=11 (11~40번째)
        int start = offset + 1;

        log.info("[SearchService] finalQuery='{}', offset={}, start={}", 
                 finalQuery, offset, start);

        // 2️⃣ 네이버 API 호출 (start 파라미터 전달)
        List<Product> products = naverClient.search(finalQuery, start);

        log.info("[SearchService] rawResultCount={}",
                products == null ? 0 : products.size());

        // 3️⃣ 중복 제거
        List<Product> deduplicated = deduplicate(products);

        log.info("[SearchService] deduplicatedCount={}",
                deduplicated == null ? 0 : deduplicated.size());

        return deduplicated;
    }

    /**
     * 검색 결과 중복 제거
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

            uniqueMap.putIfAbsent(key, product);
        }

        return new ArrayList<>(uniqueMap.values());
    }

    /**
     * title 정규화
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