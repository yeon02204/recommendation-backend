package com.example.recommendation.external.openai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.domain.explanation.CardExplanationPrompt;

@Component
public class OpenAiExplanationClientImpl implements OpenAiExplanationClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String apiKey = System.getenv("OPENAI_API_KEY");

    /**
     * 상단 공통 설명 (기존 유지)
     */
    @Override
    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return "사용자의 조건을 종합해 추천한 상품들입니다.";
    }

    /**
     * 카드별 설명 생성 (Step4)
     * - Step3 프롬프트 그대로 사용
     * - OpenAI 실제 호출
     * - 실패 시 fallback
     */
    @Override
    public Map<Long, String> generateCardExplanations(
            List<CardExplanationPrompt> prompts,
            RecommendationCriteria criteria
    ) {

        String prompt = buildCardExplanationPrompt(prompts, criteria);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "temperature", 0.7,
                    "messages", List.of(
                            Map.of("role", "system", "content", "너는 쇼핑 추천 카드 설명 생성기다."),
                            Map.of("role", "user", "content", prompt)
                    )
            );

            HttpEntity<Map<String, Object>> request =
                    new HttpEntity<>(body, headers);

            String response =
                    restTemplate.postForObject(
                            "https://api.openai.com/v1/chat/completions",
                            request,
                            String.class
                    );

            return OpenAiResponseParser.parseCardExplanationMap(response);

        } catch (Exception e) {
            // 🔥 실패해도 UX는 유지
            Map<Long, String> fallback = new HashMap<>();
            for (CardExplanationPrompt p : prompts) {
                fallback.put(
                        p.productId(),
                        "사용자의 조건과 잘 어울리는 상품입니다."
                );
            }
            return fallback;
        }
    }

    /**
     * 카드 설명용 프롬프트 (Step3 그대로)
     */
    private String buildCardExplanationPrompt(
            List<CardExplanationPrompt> prompts,
            RecommendationCriteria criteria
    ) {
        StringBuilder sb = new StringBuilder();

        sb.append("""
        너는 쇼핑 추천 서비스의 카드 설명 생성기다.

        각 상품이 왜 사용자에게 어울리는지
        서로 다른 관점으로 설명해야 한다.

        규칙:
        - 내부 점수, 순위, 정책 언급 금지
        - "가성비 최고", "1위 상품" 같은 표현 금지
        - 사용자 상황을 이해한 것처럼 자연스럽게 말할 것
        - 카드마다 다른 이유를 제시할 것
        - 상품당 1~2문장
        - JSON 형태로만 응답
        """);

        sb.append("\n[사용자 조건]\n");
        sb.append(criteria.toString()).append("\n");

        sb.append("\n[추천 상품 목록]\n");

        for (CardExplanationPrompt p : prompts) {
            sb.append("""
            - 상품 ID: %d
            - 상품명: %s
            - 맞은 조건 키워드: %s
            - 브랜드 선호 반영: %s

            """.formatted(
                    p.productId(),
                    p.title(),
                    p.matchedOptionKeywords(),
                    p.brandMatched() ? "예" : "아니오"
            ));
        }

        sb.append("""
        응답 형식:
        {
          "상품ID": "설명 문장"
        }
        """);

        return sb.toString();
    }
}
