//package com.example.recommendation.domain.criteria;
//
//import com.example.recommendation.dto.AiCriteriaResultDto;
//import com.example.recommendation.external.openai.OpenAIClient;
//import org.springframework.stereotype.Service;

/**
 * [역할]
 * - 사용자 자연어 입력을 분석하여 RecommendationCriteria를 생성한다.
 *
 * [중요 원칙]
 * - 이 클래스는 "기준 생성"만 책임진다.
 * - 추천 가능 / 불가능 판단 ❌
 * - REQUERY / INVALID 결정 ❌
 * - Decision 로직 ❌
<<<<<<< HEAD
 * - AI가 뽑아낸 키워드를 정리하는 곳
=======
 * - ai가 뽑아낸 키워드를 정리하는곳
>>>>>>> dc27fc8c838c3da42f96f953d4d0964d1352dd16
 *
 * [허용]
 * - 문자열 패턴 기반 규칙
 * - 키워드 존재 여부 판단
 *
 * [금지]
 * - OpenAI 호출 ❌
 * - 외부 API 호출 ❌
 * - 점수 계산 ❌
 * - confidence / followUpQuestion 설정 ❌
 */


package com.example.recommendation.domain.criteria;

import com.example.recommendation.dto.AiCriteriaResultDto;
import com.example.recommendation.external.openai.OpenAiCriteriaClient;
import org.springframework.stereotype.Service;

/**
 * [역할]
 * - 사용자 자연어 입력을 OpenAI에 전달
 * - 구조화된 조건을 RecommendationCriteria로 변환
 *
 * [절대 하지 않는 것]
 * - 문자열 contains 판단 ❌
 * - 가격/브랜드 해석 ❌
 * - 추천 가능성 판단 ❌
 */
@Service
public class CriteriaService {

    private final OpenAiCriteriaClient openAiClient;

    public CriteriaService(OpenAiCriteriaClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public RecommendationCriteria createCriteria(String userInput) {

        // 1️⃣ 사용자 입력을 그대로 OpenAI에 전달
        AiCriteriaResultDto aiResult =
                openAiClient.extractCriteria(userInput);

        // 2️⃣ AI 응답을 도메인 객체로 "그대로" 변환
        return new RecommendationCriteria(
                aiResult.getSearchKeyword(),
                aiResult.getOptionKeywords(),
                aiResult.getPriceMax(),
                aiResult.getPreferredBrand()
        );
    }
}