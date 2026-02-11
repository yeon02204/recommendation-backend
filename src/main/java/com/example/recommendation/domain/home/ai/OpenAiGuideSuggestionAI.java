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

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
@Primary
public class OpenAiGuideSuggestionAI implements GuideSuggestionAI {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiGuideSuggestionAI.class);

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper objectMapper;

    public OpenAiGuideSuggestionAI() {
        this.restTemplate = new RestTemplate();
        this.apiKey = System.getenv("OPENAI_API_KEY");
        this.objectMapper = new ObjectMapper();

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY is not set");
        }
    }

    @Override
    public String generateSuggestion(
            DecisionSlot slot,
            HomeConversationState state
    ) {

        log.info("[GuideSuggestionAI] generate start - slot={}", slot);

        try {

            String prompt = buildPrompt(slot, state);
            log.debug("[GuideSuggestionAI] prompt=\n{}", prompt);

            String response = callOpenAi(prompt);
            log.debug("[GuideSuggestionAI] raw response=\n{}", response);

            String result = extractText(response);

            log.info("[GuideSuggestionAI] generated={}", result);

            return result;

        } catch (Exception e) {
            log.error("[GuideSuggestionAI] failed", e);
            return "방향을 한 번 좁혀보면 선택이 쉬워질 것 같아요. 어떤 쪽이 더 끌리세요?";
        }
    }

    private String buildPrompt(
            DecisionSlot slot,
            HomeConversationState state
    ) {

        String knownInfo = state.describeConfirmedSlots();

        return """
너는 쇼핑 상담 방향 제시자다.

상황:
- 사용자가 아래 슬롯에서 막힌 상태다.
- 이 슬롯에 대해 "모르겠다"라고 답했다.

막힌 슬롯:
%s

이미 확인된 정보:
%s

역할:
- 선택의 축을 제시한다.
- 보통 사람들이 선택하는 2~3개의 방향을 알려준다.
- 마지막에 어떤 방향이 더 끌리는지 질문 1개를 던진다.

절대 금지:
- 상품명
- 브랜드명
- 가격
- 모델명
- 확정 추천

존댓말로 작성하라.
""".formatted(slot.name(), knownInfo);
    }

    private String callOpenAi(String promptText) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "temperature", 0.6,
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
        JsonNode choices = root.path("choices");

        if (!choices.isArray() || choices.isEmpty()) {
            throw new IllegalStateException("Invalid OpenAI response");
        }

        return choices.get(0)
                .path("message")
                .path("content")
                .asText();
    }
}
