package com.example.recommendation.external.openai;

import com.example.recommendation.dto.AiCriteriaResultDto;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * OpenAI 응답 파싱 전용
 *
 * [격리 전략 설명]
 * - Spring Boot 4.x + Jackson 최신 버전에서
 *   JsonNode 접근자 API는 전면 deprecated 상태
 * - 본 클래스는 해당 deprecated 사용을
 *   "의도적으로 한 지점에 격리"하기 위한 전용 위치다.
 *
 * 의미 해석 ❌
 * 기본값 처리 ❌
 * fallback ❌
 */
public class OpenAiResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("deprecation") // 🔒 의도적 격리
    public static AiCriteriaResultDto parseCriteria(String response) {
        try {
            JsonNode root = objectMapper.readTree(response);

            String content =
                    root.path("choices")
                        .get(0)
                        .path("message")
                        .path("content")
                        .asText(); // deprecated지만 의미적으로 가장 중립

            return objectMapper.readValue(content, AiCriteriaResultDto.class);

        } catch (Exception e) {
            // Criteria 계층에서 판단하지 않는다
            throw new RuntimeException(e);
        }
    }
    public static String parseExplanation(String response) {
        try {
            return objectMapper.readTree(response)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}