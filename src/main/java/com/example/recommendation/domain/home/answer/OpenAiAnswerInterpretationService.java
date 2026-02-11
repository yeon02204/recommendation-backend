package com.example.recommendation.domain.home.answer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.external.openai.OpenAiHomeClient;

/**
 * AI 기반 답변 해석 서비스
 *
 * [역할]
 * - OpenAiHomeClient 호출
 * - 사용자 발화 → AnswerInterpretation
 *
 * [절대 금지]
 * - 슬롯 결정 ❌
 * - 상태 변경 ❌
 * - AI 호출은 하지만 판단은 안 함 (AI 결과를 그대로 전달)
 */
@Service
@Primary
public class OpenAiAnswerInterpretationService {
    
    private static final Logger log =
            LoggerFactory.getLogger(OpenAiAnswerInterpretationService.class);
    
    private final OpenAiHomeClient openAiClient;
    
    // Fallback용으로 패턴 기반 서비스 보유
    private final PatternBasedAnswerInterpretationService patternBasedService;
    
    public OpenAiAnswerInterpretationService(
            OpenAiHomeClient openAiClient,
            PatternBasedAnswerInterpretationService patternBasedService
    ) {
        this.openAiClient = openAiClient;
        this.patternBasedService = patternBasedService;
    }
    
    public AnswerInterpretation interpret(
            String userInput,
            DecisionSlot lastAskedSlot
    ) {
        
        log.info("[OpenAiAnswerInterpretation] input={}, lastAsked={}", 
                userInput, lastAskedSlot);
        
        try {
            AnswerInterpretation result = 
                    openAiClient.interpretAnswer(userInput, lastAskedSlot);
            
            log.info("[OpenAiAnswerInterpretation] intent={}, value={}", 
                    result.getPrimaryIntent(), 
                    result.getNormalizedValue());
            
            return result;
            
        } catch (Exception e) {
            log.error("[OpenAiAnswerInterpretation] AI failed, fallback to pattern", e);
            
            // Fallback: 패턴 기반
            return patternBasedService.interpret(userInput);
        }
    }
}