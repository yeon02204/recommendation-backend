//package com.example.recommendation.external.openai;
//
//import com.example.recommendation.dto.AiCriteriaResultDto;
//
///**
// * Criteria / Explanation 전용 OpenAI Client
// */
//public interface OpenAiClient {
//
//    /**
//     * 사용자 자연어를 추천 기준 구조로 변환한다.
//     * 판단 ❌ / 추천 ❌
//     */
//    AiCriteriaResultDto extractCriteria(String userInput);
//    
//}


//package com.example.recommendation.external.openai;
//
//import java.util.List;
//
//import com.example.recommendation.domain.criteria.RecommendationCriteria;
//import com.example.recommendation.domain.evaluation.EvaluatedProduct;
//import com.example.recommendation.dto.AiCriteriaResultDto;
//
///**
// * Criteria / Explanation 전용 OpenAI Client
// *
// * - 판단 ❌
// * - 추천 ❌
// * - 단순 변환만 담당
// */
//public interface OpenAiClient {
//
//    /**
//     * 사용자 자연어 → 추천 기준 구조화
//     */
//    AiCriteriaResultDto extractCriteria(String userInput);
//
//    /**
//     * 추천 결과 → 설명 문장 생성
//     */
//    String generateExplanation(
//            List<EvaluatedProduct> products,
//            RecommendationCriteria criteria
//    );
//}




package com.example.recommendation.external.openai;

import com.example.recommendation.dto.AiCriteriaResultDto;

public interface OpenAiCriteriaClient {

    AiCriteriaResultDto extractCriteria(String userInput);
}