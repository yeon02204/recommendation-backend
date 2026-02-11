package com.example.recommendation.domain.home.ai;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.recommendation.domain.home.prompt.HomeReadySummaryPrompt;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


/**
 * AI 기반 READY 요약 생성 구현체
 *
 * [역할]
 * - 프롬프트 → OpenAI 호출 → 문장 반환
 *
 * [절대 금지]
 * - 판단 ❌
 * - 상태 변경 ❌
 */
@Service
@Primary
public class OpenAiReadySummaryAI implements ReadySummaryAI {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiReadySummaryAI.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public OpenAiReadySummaryAI(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.apiKey = System.getenv("OPENAI_API_KEY");
        this.objectMapper = objectMapper;

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set");
        }
    }

    @Override
    public String generate(HomeReadySummaryPrompt prompt) {

        log.info("[ReadySummaryAI] generate summary from prompt");

        try {
            String promptText = prompt.toPromptText();
            String response = callOpenAi(promptText);
            String summary = extractTextContent(response);

            log.info("[ReadySummaryAI] generated: {}", summary);
            return summary;

        } catch (Exception e) {
            log.error("[ReadySummaryAI] AI failed, fallback", e);
            return "말씀해주신 조건을 바탕으로 상품을 찾아볼게요.";
        }
    }

    private String callOpenAi(String promptText) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 0.5,
                "messages", List.of(
                        Map.of("role", "user", "content", promptText)
                )
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        return restTemplate.postForObject(
                "https://api.openai.com/v1/chat/completions",
                request,
                String.class
        );
    }

    private String extractTextContent(String response) throws Exception {

        JsonNode root = objectMapper.readTree(response);

        JsonNode choices = root.path("choices");
        if (!choices.isArray() || choices.size() == 0) {
            throw new IllegalStateException("Invalid OpenAI response");
        }

        JsonNode message = choices.get(0).path("message");
        if (message.isMissingNode()) {
            throw new IllegalStateException("Invalid OpenAI message node");
        }

        return message.path("content").asText();
    }
}
