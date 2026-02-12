package com.example.recommendation.external.openai;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.home.answer.AnswerIntent;
import com.example.recommendation.domain.home.answer.AnswerInterpretation;
import com.example.recommendation.domain.home.answer.SecondarySignal;
import com.example.recommendation.domain.home.prompt.HomeAnswerInterpretationPrompt;
import com.example.recommendation.domain.home.prompt.HomeGuidePrompt;
import com.example.recommendation.domain.home.prompt.HomeQuestionPrompt;
import com.example.recommendation.domain.home.prompt.HomeReadySummaryPrompt;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * HOME 단계 AI 호출 구현체
 *
 * [역할]
 * - 프롬프트 → OpenAI 호출 → 응답 파싱
 *
 * [절대 금지]
 * - 판단 ❌
 * - 상태 변경 ❌
 */
@Component
public class OpenAiHomeClientImpl implements OpenAiHomeClient {
    
    private static final Logger log = 
            LoggerFactory.getLogger(OpenAiHomeClientImpl.class);
    
    private final RestTemplate restTemplate;
    private final String apiKey;
    private final ObjectMapper objectMapper;
    
    public OpenAiHomeClientImpl() {
        this.restTemplate = new RestTemplate();
        this.apiKey = System.getenv("OPENAI_API_KEY");
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public String generateQuestion(
            DecisionSlot slot,
            HomeConversationState state
    ) {
        
        log.info("[OpenAiHome] generateQuestion slot={}", slot);
        
        HomeQuestionPrompt prompt = new HomeQuestionPrompt(slot, state);
        
        try {
            String response = callOpenAi(prompt.toPromptText(), 0.4);  // 🔥 0.3 → 0.4
            String question = extractTextContent(response);
            
            log.info("[OpenAiHome] generated question: {}", question);
            return question;
            
        } catch (Exception e) {
            log.error("[OpenAiHome] question generation failed", e);
            return getFallbackQuestion(slot);
        }
    }
    
    @Override
    public String generateGuide(
            DecisionSlot slot,
            HomeConversationState state
    ) {
        
        log.info("[OpenAiHome] generateGuide slot={}", slot);
        
        HomeGuidePrompt prompt = new HomeGuidePrompt(slot, state);
        
        try {
            String response = callOpenAi(prompt.toPromptText(), 0.5);  // 🔥 0.7 → 0.5
            String guide = extractTextContent(response);
            
            log.info("[OpenAiHome] generated guide: {}", guide);
            return guide;
            
        } catch (Exception e) {
            log.error("[OpenAiHome] guide generation failed", e);
            return getFallbackGuide(slot);
        }
    }
    
    @Override
    public String generateReadySummary(
            RecommendationCriteria criteria
    ) {
        
        log.info("[OpenAiHome] generateReadySummary");
        
        HomeReadySummaryPrompt prompt = new HomeReadySummaryPrompt(criteria);
        
        try {
            String response = callOpenAi(prompt.toPromptText(), 0.4);  // 🔥 0.5 → 0.4
            String summary = extractTextContent(response);
            
            log.info("[OpenAiHome] generated summary: {}", summary);
            return summary;
            
        } catch (Exception e) {
            log.error("[OpenAiHome] summary generation failed", e);
            return "말씀해주신 조건을 바탕으로 상품을 찾아볼게요.";
        }
    }
    
    @Override
    public AnswerInterpretation interpretAnswer(
            String userInput,
            DecisionSlot lastAskedSlot
    ) {
        
        log.info("[OpenAiHome] interpretAnswer input={}", userInput);
        
        HomeAnswerInterpretationPrompt prompt = 
                new HomeAnswerInterpretationPrompt(userInput, lastAskedSlot);
        
        try {
            String response = callOpenAi(prompt.toPromptText(), 0.0);
            return parseAnswerInterpretation(response);
            
        } catch (Exception e) {
            log.error("[OpenAiHome] answer interpretation failed", e);
            // Fallback: ANSWER로 처리
            return new AnswerInterpretation(
                    AnswerIntent.ANSWER,
                    userInput
            );
        }
    }
    
    /* =========================
     * Private Helpers
     * ========================= */
    
    private String callOpenAi(String prompt, double temperature) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini",
                "temperature", temperature,
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)
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
        return root.path("choices")
                .get(0)
                .path("message")
                .path("content")
                .asText();
    }
    
    private AnswerInterpretation parseAnswerInterpretation(String response) 
            throws Exception {
        
        String content = extractTextContent(response);
        
        // JSON 파싱
        JsonNode json = objectMapper.readTree(content);
        
        String intentStr = json.path("primaryIntent").asText();
        AnswerIntent intent = AnswerIntent.valueOf(intentStr);
        
        String normalizedValue = json.path("normalizedValue").asText();
        
        List<SecondarySignal> signals = new ArrayList<>();
        JsonNode signalsNode = json.path("secondarySignals");
        
        if (signalsNode.isArray()) {
            for (JsonNode signal : signalsNode) {
                String slotStr = signal.path("targetSlot").asText();
                String value = signal.path("value").asText();
                
                DecisionSlot slot = DecisionSlot.valueOf(slotStr);
                signals.add(new SecondarySignal(slot, value));
            }
        }
        
        return new AnswerInterpretation(intent, normalizedValue, signals);
    }
    
    /* =========================
     * Fallback Messages
     * ========================= */
    
    private String getFallbackQuestion(DecisionSlot slot) {
        return switch (slot) {
            case TARGET -> "누구를 위한 상품인가요?";
            case PURPOSE -> "어떤 용도로 사용할 예정인가요?";
            case CONSTRAINT -> "피하고 싶은 점이 있을까요?";
            case PREFERENCE -> "선호하는 스타일이나 성향이 있을까요?";
            case BUDGET -> "예산은 어느 정도로 생각하고 계신가요?";
            case CONTEXT -> "어떤 상황에서 쓰실 예정인가요?";
        };
    }
    
    private String getFallbackGuide(DecisionSlot slot) {
        return switch (slot) {
            case TARGET -> 
                "보통 누구를 위한 상품인지부터 정하면 선택이 쉬워져요.";
            case PURPOSE -> 
                "사용 목적을 정하면 추천 범위를 많이 줄일 수 있어요.";
            case CONSTRAINT -> 
                "피하고 싶은 조건이 있으면 먼저 정하는 것도 좋아요.";
            case PREFERENCE -> 
                "스타일이나 취향을 기준으로 방향을 잡아볼 수도 있어요.";
            case BUDGET -> 
                "대략적인 예산 범위를 정하면 선택이 훨씬 쉬워져요.";
            case CONTEXT -> 
                "어떤 상황에서 쓰는지에 따라 추천 방향이 달라질 수 있어요.";
        };
    }
}