package com.example.recommendation.service;

import com.example.recommendation.dto.RecommendationResponseDto;
import org.springframework.stereotype.Service;

@Service
public class RecommendationService {

    public RecommendationResponseDto recommend() {
        // TODO: 나중에 진짜 추천 로직
        return new RecommendationResponseDto("추천 결과 (임시)");
    }
}
