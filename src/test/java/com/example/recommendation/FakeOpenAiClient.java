package com.example.recommendation;

import com.example.recommendation.dto.AiCriteriaResultDto;
import com.example.recommendation.external.openai.OpenAiCriteriaClient;

import java.util.List;

class FakeOpenAiClient implements OpenAiCriteriaClient {

    @Override
    public AiCriteriaResultDto extractCriteria(String userInput) {
        AiCriteriaResultDto dto = new AiCriteriaResultDto();

        dto.setSearchKeyword(userInput);
        dto.setOptionKeywords(List.of());
        dto.setPriceMax(null);
        dto.setPreferredBrand(null);

        return dto;
    }
}