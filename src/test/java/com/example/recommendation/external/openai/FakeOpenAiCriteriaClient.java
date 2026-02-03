package com.example.recommendation.external.openai;

import com.example.recommendation.dto.AiCriteriaResultDto;
import com.example.recommendation.external.openai.OpenAiCriteriaClient;

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

        // ❌ userInput 사용 금지
        dto.setSearchKeyword("노트북");
        dto.setOptionKeywords(List.of("가벼운"));
        dto.setPriceMax(1000000);
        dto.setPreferredBrand(null);

        return dto;
    }
}