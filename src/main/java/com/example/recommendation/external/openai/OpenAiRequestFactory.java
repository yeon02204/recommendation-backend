package com.example.recommendation.external.openai;

import java.util.List;
import java.util.Map;

/**
 * OpenAI HTTP 요청 바디 생성 전용
 *
 * 정책 ❌
 * 의미 ❌
 * 순수 기술 코드
 */
public class OpenAiRequestFactory {

    public static Map<String, Object> criteriaRequest(String prompt, String apiKey) {

        return Map.of(
                "model", "gpt-4o-mini",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 상품 추천 시스템의 조건 추출기다."),
                        Map.of("role", "user", "content", prompt)
                ),
                "temperature", 0
        );
    }
    
    public static Object explanationRequest(String prompt, String apiKey) {
        return Map.of(
            "model", "gpt-4o-mini",
            "messages", List.of(
                Map.of(
                    "role", "user",
                    "content", prompt
                )
            ),
            "temperature", 0.7
        );
    }
}


