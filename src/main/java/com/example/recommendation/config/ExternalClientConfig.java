package com.example.recommendation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.recommendation.external.naver.FakeNaverClient;
import com.example.recommendation.external.naver.NaverClient;

@Configuration
public class ExternalClientConfig {

    @Bean
    public NaverClient naverClient() {
        return new FakeNaverClient();
    }

    // ❌ OpenAiExplanationClient Bean 정의 없음
}