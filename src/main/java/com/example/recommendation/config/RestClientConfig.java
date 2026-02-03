package com.example.recommendation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 외부 HTTP API 호출을 위한 클라이언트 설정
 *
 * - OpenAI
 * - Naver
 *
 * 정책 ❌
 * 도메인 ❌
 * 순수 인프라 설정
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}