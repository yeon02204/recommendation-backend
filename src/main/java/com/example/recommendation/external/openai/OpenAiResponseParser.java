package com.example.recommendation.external.openai;

import java.util.Map;

import com.example.recommendation.dto.AiCriteriaResultDto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * OpenAI 응답 파싱 전용
 *
 * [격리 전략]
 * - JsonNode 직접 조작 ❌
 * - 의미 해석 ❌
 * - 판단 ❌
 * - fallback ❌
 */
public class OpenAiResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /* =========================
       Criteria 파싱 (기존)
       ========================= */

    public static AiCriteriaResultDto parseCriteria(String response) {
        try {
            String content = objectMapper.readTree(response)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return objectMapper.readValue(content, AiCriteriaResultDto.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* =========================
       상단 설명 파싱 (기존)
       ========================= */

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

    /* =========================
       카드 설명 파싱 (🔥 안정판)
       ========================= */

    public static Map<Long, String> parseCardExplanationMap(String response) {
        try {
            String content = objectMapper.readTree(response)
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // 🔑 핵심: JsonNode 순회 ❌ → Map 직접 파싱
            Map<String, String> raw =
                    objectMapper.readValue(
                            content,
                            new TypeReference<Map<String, String>>() {}
                    );

            // key String → Long 변환
            return raw.entrySet().stream()
                    .collect(
                            java.util.stream.Collectors.toMap(
                                    e -> Long.valueOf(e.getKey()),
                                    Map.Entry::getValue
                            )
                    );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
