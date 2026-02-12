package com.example.recommendation.external.naver;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.recommendation.external.naver.dto.Product;

/**
 * [역할]
 * - 네이버 쇼핑 API 호출 전용 (prod 프로파일)
 *
 * [정책]
 * - query는 SearchService에서 완성된 문자열 그대로 사용
 * - 이 클래스는 인코딩 + 호출만 담당
 * 
 * 🔥 2025-02-09 업데이트:
 * - start 파라미터 지원 (RETRY_SEARCH)
 */
@Component

public class RealNaverClient implements NaverClient {

    private static final Logger log =
            LoggerFactory.getLogger(RealNaverClient.class);

    private static final int DEFAULT_DISPLAY = 30;
    private static final int DEFAULT_START = 1;

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public RealNaverClient(
            @Value("${naver.client-id}") String clientId,
            @Value("${naver.client-secret}") String clientSecret
    ) {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        this.restTemplate = new RestTemplate(factory);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public List<Product> search(String keyword) {
        return search(keyword, DEFAULT_START);
    }

    @Override
    public List<Product> search(String keyword, int start) {

        log.info("[RealNaverClient] search - keyword='{}', start={}", 
                 keyword, start);

        try {
            // 1️⃣ 인증 헤더
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Naver-Client-Id", clientId);
            headers.add("X-Naver-Client-Secret", clientSecret);

            // 2️⃣ URL 구성 (🔥 start 파라미터 추가!)
            String url = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com/v1/search/shop.json")
                    .queryParam("query", keyword)   // 한글 그대로
                    .queryParam("display", DEFAULT_DISPLAY)
                    .queryParam("start", start)     // 🔥 추가!
                    .queryParam("sort", "sim")
                    .build(false) 
                    .toUriString();

            log.debug("[RealNaverClient] API URL: {}", url);

            HttpEntity<Void> request = new HttpEntity<>(headers);

            // 3️⃣ 호출
            ResponseEntity<NaverSearchResponse> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            request,
                            NaverSearchResponse.class
                    );

            // 4️⃣ 매핑
            NaverSearchResponse body = response.getBody();
            if (body == null || body.getItems() == null) {
                log.warn("[RealNaverClient] Empty response");
                return List.of();
            }

            List<Product> products = body.getItems().stream()
                    .map(this::mapToProduct)
                    .toList();

            log.info("[RealNaverClient] Found {} products", products.size());

            return products;

        } catch (Exception e) {
            log.error("[RealNaverClient] API call failed - keyword='{}', start={}", 
                      keyword, start, e);
            return List.of();
        }
    }

    private Product mapToProduct(NaverItem item) {
        return new Product(
                safeParseLong(item.getProductId()),
                stripHtml(item.getTitle()),
                item.getBrand(),
                item.getImage(),
                item.getLink(),
                item.getLprice(),    // 🔥 추가
                item.getMallName()   // 🔥 추가
        );
    }

    private Long safeParseLong(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String stripHtml(String text) {
        return text == null ? null : text.replaceAll("<[^>]*>", "");
    }
}