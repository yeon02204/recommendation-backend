package com.example.recommendation.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.recommendation.dto.RecommendationRequestDto;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.orchestrator.HomeRecommendationOrchestrator;

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
 */
@RestController
@RequestMapping("/api/recommend")
public class RecommendationController {
    
    private final HomeRecommendationOrchestrator homeOrchestrator;
    
    // 대화 컨텍스트 저장 (세션 관리용)
    // 실제 프로덕션에서는 Redis, HttpSession, 또는 세션 스토어 사용 권장
    private final Map<String, Object> conversationContext = new HashMap<>();
    
    public RecommendationController(HomeRecommendationOrchestrator homeOrchestrator) {
        this.homeOrchestrator = homeOrchestrator;
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
     * 
     * 프론트에서 "새로고침" 버튼 또는 F5 클릭 시 호출
     * 대화 히스토리, 세션 상태 등을 모두 초기화
     */
    @PostMapping("/reset")
    public ResponseEntity<?> reset() {
        try {
            // 1. 대화 컨텍스트 초기화
            conversationContext.clear();
            System.out.println("[RESET] 대화 컨텍스트 초기화 완료");
            
            // 2. 세션 데이터 초기화 (HttpSession 사용 시)
            // session.invalidate();
            
            // 3. 임시 저장된 사용자 상태 초기화 (서비스 레이어에 있다면)
            // userStateService.clearAll();
            
            // 4. AI 대화 히스토리 초기화 (OpenAI context 등)
            // aiService.resetContext();
            
            System.out.println("[RESET] 서버 세션이 초기화되었습니다.");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "서버가 초기화되었습니다.");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("[RESET ERROR] " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "초기화 중 오류가 발생했습니다: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }
}