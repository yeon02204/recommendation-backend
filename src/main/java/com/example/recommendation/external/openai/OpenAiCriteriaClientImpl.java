package com.example.recommendation.external.openai;

import com.example.recommendation.dto.AiCriteriaResultDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Criteria AI 전용 OpenAI 클라이언트
 *
 * 책임:
 * 1. 프롬프트 생성
 * 2. OpenAI HTTP 호출
 * 3. 응답 JSON → DTO 변환
 *
 * 판단 ❌
 * 보정 ❌
 * fallback ❌
 */
@Component
public class OpenAiCriteriaClientImpl implements OpenAiCriteriaClient {

    private final RestTemplate restTemplate;
    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public OpenAiCriteriaClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AiCriteriaResultDto extractCriteria(String userInput) {

        String prompt = buildPrompt(userInput);

        // ✅ Header 구성 (핵심)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // ✅ Body + Header 결합
        HttpEntity<Object> requestEntity =
                new HttpEntity<>(
                        OpenAiRequestFactory.criteriaRequest(prompt, apiKey),
                        headers
                );

        String response =
                restTemplate.postForObject(
                        "https://api.openai.com/v1/chat/completions",
                        requestEntity,
                        String.class
                );

        // JSON → DTO 변환만 수행
        return OpenAiResponseParser.parseCriteria(response);
    }

    /**
     * Criteria AI 계약 프롬프트
     * (고도화 전까지 수정 금지)
     */
    private String buildPrompt(String userInput) {
        return """
            너는 상품 추천 시스템의 "조건 추출기"다.

            사용자의 문장을 분석해서
            아래 JSON 형식으로만 응답하라.

            추측 금지.
            값이 없으면 null 또는 빈 배열을 사용하라.
            설명 문장 금지.
            JSON 외 출력 금지.

            사용자 입력:
            "%s"

            아래 JSON 스키마를 정확히 따라라:

            {
              "searchKeyword": string | null,
              "optionKeywords": string[],
              "priceMax": number | null,
              "preferredBrand": string | null
            }
            """.formatted(userInput);
    }
}
