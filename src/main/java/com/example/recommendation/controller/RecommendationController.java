package com.example.recommendation.controller;

import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.service.RecommendationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/recommend")
    public RecommendationResponseDto recommend() {
        return recommendationService.recommend();
    }
}
