package com.example.recommendation.domain.criteria;

import com.example.recommendation.dto.AiCriteriaResultDto;
import com.example.recommendation.external.openai.OpenAiCriteriaClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * [역할]
 * - 사용자 자연어 입력을 OpenAI에 전달
 * - 구조화된 조건을 RecommendationCriteria로 변환
 *
 * [핵심 책임]
 * - AI 결과를 도메인 규칙에 맞게 정규화한다
 *
 * [절대 하지 않는 것]
 * - 추천 가능성 판단 ❌
 * - 점수 계산 ❌
 * - Decision 로직 ❌
 */
@Service
public class CriteriaService {

    private final OpenAiCriteriaClient openAiClient;

    public CriteriaService(OpenAiCriteriaClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public RecommendationCriteria createCriteria(String userInput) {

        // 1️⃣ 사용자 입력 → AI 추출
        AiCriteriaResultDto aiResult =
                openAiClient.extractCriteria(userInput);

        String searchKeyword = aiResult.getSearchKeyword();
        String preferredBrand = aiResult.getPreferredBrand();

        // 2️⃣ optionKeywords 방어적 복사
        List<String> optionKeywords = new ArrayList<>(
                aiResult.getOptionKeywords()
        );

        // 3️⃣ 🔥 도메인 정규화 규칙
        // preferredBrand가 있으면 optionKeywords에서 제거
        if (preferredBrand != null && !preferredBrand.isBlank()) {
            optionKeywords.removeIf(
                    keyword -> keyword.equalsIgnoreCase(preferredBrand)
            );
        }

        // 4️⃣ 도메인 객체 생성
        return new RecommendationCriteria(
                searchKeyword,
                optionKeywords,
                aiResult.getPriceMax(),
                preferredBrand
        );
    }
}
