package com.example.recommendation.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.recommendation.dto.RecommendationRequestDto;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.orchestrator.HomeRecommendationOrchestrator;
import com.example.recommendation.domain.criteria.ConversationContextService;

@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {

    private final HomeRecommendationOrchestrator homeOrchestrator;
    private final ConversationContextService contextService;

    public RecommendationController(
            HomeRecommendationOrchestrator homeOrchestrator,
            ConversationContextService contextService
    ) {
        this.homeOrchestrator = homeOrchestrator;
        this.contextService = contextService;
    }

    @PostMapping("/home")
    public RecommendationResponseDto recommendHome(
            @RequestBody RecommendationRequestDto request
    ) {
        System.out.println("🔥 Controller 진입");
        System.out.println("🔥 userInput = " + request.getUserInput());

        if (request == null || request.getUserInput() == null || request.getUserInput().isBlank()) {
            return RecommendationResponseDto.invalid("입력이 비어 있습니다.");
        }

        return homeOrchestrator.handle(request);
    }

    /**
     * 세션 리셋 엔드포인트
     * POST /api/recommend/reset
     */
    @PostMapping("/reset")
    public ResponseEntity<?> reset() {

        // 🔥 진짜 상태 초기화
        contextService.reset();

        System.out.println("🔥 ConversationContextService 초기화 완료");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "서버 대화 상태가 초기화되었습니다.");

        return ResponseEntity.ok(response);
    }
}
