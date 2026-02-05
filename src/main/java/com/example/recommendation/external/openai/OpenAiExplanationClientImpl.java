package com.example.recommendation.external.openai;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;

@Component
public class OpenAiExplanationClientImpl implements OpenAiExplanationClient {

    private static final Logger log =
            LoggerFactory.getLogger(OpenAiExplanationClientImpl.class);

    private final RestTemplate restTemplate;
    private final String apiKey = System.getenv("OPENAI_API_KEY");

    public OpenAiExplanationClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {

        String prompt = buildPrompt(products, criteria);

        try {
            // Header
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Object> requestEntity =
                    new HttpEntity<>(
                            OpenAiRequestFactory.explanationRequest(prompt, apiKey),
                            headers
                    );

            String response =
                    restTemplate.postForObject(
                            "https://api.openai.com/v1/chat/completions",
                            requestEntity,
                            String.class
                    );

            return OpenAiResponseParser.parseExplanation(response);

        } catch (Exception e) {
            // 🔥 핵심: 설명 생성 실패는 "치명적 오류"가 아님
            log.error("OpenAI explanation generation failed", e);

            // ✅ fallback 설명 (UX 유지)
            return "사용자의 조건에 맞는 상품들을 기준으로 추천한 결과입니다.";
        }
    }

    /**
     * Explanation AI 계약 프롬프트
     * (고도화 전 수정 금지)
     */
    private String buildPrompt(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        return """
            너는 상품 추천 결과에 대한 설명 생성기다.

            아래 추천 결과와 사용자의 조건을 바탕으로
            한 문단의 자연스러운 설명 문장만 생성하라.

            판단, 점수 설명, 정책 언급 금지.
            추천 이유만 서술하라.

            사용자 조건:
            %s

            추천 결과:
            %s
            """.formatted(criteria, products);
    }
}
