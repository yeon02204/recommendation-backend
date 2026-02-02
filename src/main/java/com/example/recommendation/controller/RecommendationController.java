package com.example.recommendation.controller;

import com.example.recommendation.dto.RecommendationRequestDto;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.orchestrator.HomeRecommendationOrchestrator;
import org.springframework.web.bind.annotation.*;
/**
 * [역할]
 * - HTTP 요청의 진입점
 * - 사용자 입력을 받아 RecommendationService로 전달
 *
 * [이 클래스에서 하면 안 되는 것]
 * - 추천 로직 ❌
 * - 조건 판단 ❌
 * - AI 호출 ❌
 *
 * [흐름]
 * 사용자 요청
 *  → recommend(userInput)
 *  → RecommendationService.recommend 호출
 *
 * TODO:
 * 1. (선택) GET → POST 변경 (JSON body)
 * 2. (선택) 사용자 세션/대화 ID 추가
 * 3. (선택) 예외 처리 (빈 입력 등)
 */

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final HomeRecommendationOrchestrator homeOrchestrator;

    public RecommendationController(HomeRecommendationOrchestrator homeOrchestrator) {
        this.homeOrchestrator = homeOrchestrator;
    }
    
    
    @PostMapping("/home")
    public RecommendationResponseDto recommendHome(
            @RequestBody RecommendationRequestDto request
    ) {
        if (request == null || request.getUserInput() == null || request.getUserInput().isBlank()) {
            return RecommendationResponseDto.invalid("입력이 비어 있습니다.");
        }
        return homeOrchestrator.handle(request);
    }

}


