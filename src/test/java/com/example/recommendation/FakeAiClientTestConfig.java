package com.example.recommendation;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.example.recommendation.external.openai.FakeOpenAiCriteriaClient;
import com.example.recommendation.external.openai.FakeOpenAiExplanationClient;
import com.example.recommendation.external.openai.OpenAiCriteriaClient;
import com.example.recommendation.external.openai.OpenAiExplanationClient;

/**
 * 테스트 전용 AI Client 설정
 *
 * 실제 OpenAI Bean을 Fake로 교체한다.
 * 정책 의미 변경 ❌
 */
@TestConfiguration
public class FakeAiClientTestConfig {

    @Bean
    public OpenAiCriteriaClient openAiCriteriaClient() {
        return new FakeOpenAiCriteriaClient();
    }

    @Bean
    public OpenAiExplanationClient openAiExplanationClient() {
        return new FakeOpenAiExplanationClient();
    }
}