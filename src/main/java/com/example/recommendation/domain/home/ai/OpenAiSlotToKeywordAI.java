package com.example.recommendation.domain.home.ai;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.state.HomeConversationState;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Primary
public class OpenAiSlotToKeywordAI implements SlotToKeywordAI {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiSlotToKeywordAI.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = System.getenv("OPENAI_API_KEY");
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String generate(HomeConversationState state) {

        try {
            log.info("🔥 SlotToKeywordAI CALLED");

            String prompt = buildPrompt(state);

            String response = callOpenAi(prompt);

            return extractText(response);

        } catch (Exception e) {
            log.error("[SlotToKeywordAI] failed", e);
            return null;
        }
    }

    private String buildPrompt(HomeConversationState state) {

        StringBuilder confirmed = new StringBuilder();

        for (DecisionSlot slot : DecisionSlot.values()) {

            SlotState s = state.getSlot(slot);

            if (s.isConfirmed() && s.getValue() != null) {
                confirmed.append("- ")
                        .append(slot.name())
                        .append(": ")
                        .append(s.getValue())
                        .append("\n");
            }
        }

        return """
너는 대화 슬롯을 네이버 쇼핑 검색 키워드로 변환하는 AI다.

입력 슬롯:
%s

규칙:
- 검색 가능한 카테고리 1개만 출력
- 너무 추상적이면 안됨 (예: "선물" ❌)
- 브랜드/가격/추천 같은 단어 금지
- 한 줄 텍스트만 출력
""".formatted(confirmed);
    }

    private String callOpenAi(String promptText) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 0.4,
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

    private String extractText(String response) throws Exception {

        JsonNode root = objectMapper.readTree(response);

        return root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText()
                .trim();
    }
}
