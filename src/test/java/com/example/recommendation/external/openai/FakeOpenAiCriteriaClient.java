package com.example.recommendation.external.openai;

import com.example.recommendation.dto.AiCriteriaResultDto;
import java.util.List;

//class FakeOpenAiCriteriaClient implements OpenAiCriteriaClient {
//
//    @Override
//    public AiCriteriaResultDto extractCriteria(String userInput) {
//        AiCriteriaResultDto dto = new AiCriteriaResultDto();
//
//        dto.setSearchKeyword(userInput);
//        dto.setOptionKeywords(List.of());
//        dto.setPriceMax(null);
//        dto.setPreferredBrand(null);
//
//        return dto;
//    }
//}


public class FakeOpenAiCriteriaClient implements OpenAiCriteriaClient {

    @Override
    public AiCriteriaResultDto extractCriteria(String userInput) {

        AiCriteriaResultDto dto = new AiCriteriaResultDto();

        // ✅ 테스트 계약을 만족하는 최소 구현
        dto.setSearchKeyword(userInput);
        dto.setOptionKeywords(List.of());
        dto.setPriceMax(null);
        dto.setPreferredBrand(null);

        return dto;
    }
}