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
     * 상단 공통 설명 (🔥 AI 호출로 교체)
     */
    @Override
    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        String prompt = buildMainExplanationPrompt(products, criteria);
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                    "model", "gpt-4o-mini",
                    "temperature", 0.5,  // 🔥 메인 요약 temperature
                    "messages", List.of(
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

            return OpenAiResponseParser.parseExplanation(response);

        } catch (Exception e) {
            // Fallback
            return "말씀해주신 조건으로 상품을 찾아봤어.";
        }
    }
    
    /**
     * 메인 요약 프롬프트 (🔥 신규)
     */
    private String buildMainExplanationPrompt(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return """
        너는 "꼬강"이라는 쇼핑 도우미야.
        꼬질한 강아지지만 눈치 빠르고 똑똑해.
        
        말투:
        - 자연스러운 반말
        - 짧게 끊어서 말해
        - 적당히 밝게 귀여운 말투
        
        절대 금지:
        - 슬롯, 시스템, 단계 같은 내부 표현
        - "조건을 종합해" 같은 추상적 표현
        - 내부 판단 과정 설명
        
        ---
        
        역할:
        - 지금까지 대화를 이해하고
        - 왜 이 상품들이 나온 건지 1~2문장으로 설명해
        
        사용자 조건:
        %s
        
        지시:
        - 사용자의 실제 상황을 언급해
        - 추상적인 문장 금지
        - 1~2문장만
        
        좋은 예:
        "친구 결혼 선물로 실용적인 주방용품 위주로 찾아봤어"
        
        나쁜 예:
        "사용자의 조건을 종합해 추천한 상품들입니다."
        """.formatted(criteria.toString());
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
                    "temperature", 0.5,  // 🔥 0.7 → 0.5
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)  // 🔥 system 제거
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
        너는 "꼬강"이라는 쇼핑 도우미야.
        꼬질한 강아지지만 눈치 빠르고 똑똑해.
        
        말투:
        - 자연스러운 반말
        - 짧게 끊어서 말해
        - 적당히 밝게 귀여운 말투
        
        절대 금지:
        - 슬롯, 시스템, 단계 같은 내부 표현
        - 점수, 순위 언급
        - "가성비 최고" 같은 과장
        
        ---
        
        역할:
        - 각 상품이 왜 이 사람한테 어울리는지 설명해
        - 서로 다른 이유로 말해
        - 상품당 1~2문장
        
        규칙:
        - 사용자 상황을 이해한 것처럼 자연스럽게
        - 카드마다 다른 관점 제시
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