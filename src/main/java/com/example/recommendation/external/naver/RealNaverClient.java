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

/**
 * [역할]
 * - 네이버 쇼핑 API 호출 전용 (prod 프로파일)
 *
 * [MVP 정책]
 * - 정렬/필터/점수/판단 없음
 * - 실패/예외 발생 시 빈 리스트 반환 (throw 금지)
 */
@Component
@Profile("prod")
public class RealNaverClient implements NaverClient {

    private static final Logger log = LoggerFactory.getLogger(RealNaverClient.class);

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public RealNaverClient(
            @Value("${naver.client-id}") String clientId,
            @Value("${naver.client-secret}") String clientSecret
    ) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(3000);

        this.restTemplate = new RestTemplate(factory);
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public List<Product> search(String keyword) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Naver-Client-Id", "QbrRNVwRknp8MzN4py2r");
            headers.add("X-Naver-Client-Secret", clientSecret);

            String url = UriComponentsBuilder
                    .fromUriString("https://openapi.naver.com/v1/search/shop.json")
                    .queryParam("query", keyword)
                    .queryParam("display", 20)
                    .toUriString();

            HttpEntity<Void> request = new HttpEntity<>(headers);

            ResponseEntity<NaverSearchResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    NaverSearchResponse.class
            );

            NaverSearchResponse body = response.getBody();
            if (body == null || body.getItems() == null) {
                return List.of();
            }

            return body.getItems().stream()
                    .map(this::mapToProduct)
                    .toList();

        } catch (Exception e) {
            log.error("Naver API call failed", e);
            return List.of(); // ✅ MVP 핵심: 실패하면 빈 리스트
        }
    }

    private Product mapToProduct(NaverItem item) {
        Long productId = safeParseLong(item.getProductId());
        String title = stripHtml(item.getTitle());
        String brand = item.getBrand(); // 없으면 null 가능
        return new Product(productId, title, brand);
    }

    private Long safeParseLong(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String stripHtml(String text) {
        if (text == null) return null;
        return text.replaceAll("<[^>]*>", "");
    }
}
