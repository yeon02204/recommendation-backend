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
 * - AI 결과를 도메인 규칙에 맞게 "정규화만" 한다
 *
 * [절대 하지 않는 것]
 * - 추천 가능성 판단 ❌
 * - intent 판단 ❌
 * - command 판단 ❌
 * - 점수 계산 ❌
 * - Decision 로직 ❌
 */
@Service
public class CriteriaService {

    private final OpenAiCriteriaClient openAiClient;

    public CriteriaService(OpenAiCriteriaClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    /**
     * ✅ 기존 메서드 (절대 유지)
     * - 모든 기존 테스트 / 호출부 호환
     *
     * [중요 변경점]
     * - intentType / commandType을 판단하지 않고
     * - OpenAI가 반환한 값을 그대로 "통과"시킨다
     */
    public RecommendationCriteria createCriteria(String userInput) {

        // 1️⃣ OpenAI에 자연어 전달 → 구조화 결과 수신
        AiCriteriaResultDto aiResult =
                openAiClient.extractCriteria(userInput);

        // 2️⃣ 조건 필드 추출
        String searchKeyword = aiResult.getSearchKeyword();
        String preferredBrand = aiResult.getPreferredBrand();

        List<String> optionKeywords = new ArrayList<>(
                aiResult.getOptionKeywords()
        );

        // 3️⃣ 브랜드명이 옵션 키워드에 섞여 있으면 제거
        // (AI가 중복으로 주는 경우 방어)
        if (preferredBrand != null && !preferredBrand.isBlank()) {
            optionKeywords.removeIf(
                    keyword -> keyword.equalsIgnoreCase(preferredBrand)
            );
        }

        // 4️⃣ 🔥 의미(intent / command)는 해석하지 않고 그대로 통과
        return new RecommendationCriteria(
                searchKeyword,
                optionKeywords,
                aiResult.getPriceMax(),
                preferredBrand,
                aiResult.getIntentType(),   // ⭕ AI 판단 그대로
                aiResult.getCommandType()   // ⭕ AI 판단 그대로
        );
    }

    /**
     * 🧠 Step 7 확장용
     * - ConversationContext를 기준으로 Criteria를 "완성형"으로 만든다
     * - 병합 판단 ❌ (ConversationContextService 책임)
     *
     * ⚠️ 현재 단계에서는 사용하지 않음
     * ⚠️ 구조 유지를 위해 남겨둔다
     */
    public RecommendationCriteria createCriteria(
            String userInput,
            ConversationContext context
    ) {

        // 기본 조건 추출은 동일
        RecommendationCriteria base =
                createCriteria(userInput);

        return new RecommendationCriteria(
                base.getSearchKeyword(),
                base.getOptionKeywords(),
                base.getPriceMax(),
                base.getPreferredBrand(),
                context.getIntentType()
        );
    }
}
