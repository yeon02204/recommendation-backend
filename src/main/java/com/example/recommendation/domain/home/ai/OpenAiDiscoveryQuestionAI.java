package com.example.recommendation.domain.home.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;
import com.example.recommendation.external.openai.OpenAiHomeClient;

/**
 * AI 기반 질문 생성 구현체
 *
 * [역할]
 * - OpenAiHomeClient 호출
 * - 슬롯별 맥락 고려한 질문 생성
 *
 * [절대 금지]
 * - 판단 ❌
 * - 상태 변경 ❌
 */
@Service
@Primary
public class OpenAiDiscoveryQuestionAI implements DiscoveryQuestionAI {
    
    private static final Logger log =
            LoggerFactory.getLogger(OpenAiDiscoveryQuestionAI.class);
    
    private final OpenAiHomeClient openAiClient;
    
    public OpenAiDiscoveryQuestionAI(OpenAiHomeClient openAiClient) {
        this.openAiClient = openAiClient;
    }
    
    @Override
    public String generateQuestion(
            DecisionSlot slot,
            HomeConversationState state
    ) {
        
        log.info("[DiscoveryQuestionAI] generate for slot={}", slot);
        
        String question = openAiClient.generateQuestion(slot, state);
        
        log.info("[DiscoveryQuestionAI] generated: {}", question);
        
        return question;
    }
}