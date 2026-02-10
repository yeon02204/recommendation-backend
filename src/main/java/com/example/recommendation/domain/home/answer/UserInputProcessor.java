package com.example.recommendation.domain.home.answer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 사용자 입력 → 슬롯 반영 통합 서비스 (STEP 10)
 *
 * [역할]
 * - 전체 파이프라인 조율
 * - AnswerInterpretationService → SlotBindingPolicy → SlotState
 *
 * [절대 금지]
 * - 판단 ❌ (정책에 위임)
 * - AI 호출 ❌
 */
@Service
public class UserInputProcessor {
    
    private static final Logger log =
            LoggerFactory.getLogger(UserInputProcessor.class);
    
    private final AnswerInterpretationService interpretationService;
    private final SlotBindingPolicy bindingPolicy;
    
    public UserInputProcessor(
            AnswerInterpretationService interpretationService,
            SlotBindingPolicy bindingPolicy
    ) {
        this.interpretationService = interpretationService;
        this.bindingPolicy = bindingPolicy;
    }
    
    /**
     * 사용자 입력 → 슬롯 상태 반영
     */
    public void processUserInput(
            String userInput,
            HomeConversationState state
    ) {
        
        log.info("[UserInputProcessor] input: {}", userInput);
        
        // 1. 발화 의도 분류
        AnswerInterpretation interpretation =
                interpretationService.interpret(userInput);
        
        log.info("[UserInputProcessor] intent: {}, value: {}",
                interpretation.getPrimaryIntent(),
                interpretation.getNormalizedValue());
        
        // 2. 슬롯 귀속 결정
        PendingQuestionContext questionContext = state.getQuestionContext();
        
        List<SlotUpdateCommand> commands =
                bindingPolicy.decide(interpretation, questionContext, state);
        
        log.info("[UserInputProcessor] commands: {}", commands.size());
        
        // 3. 명령 실행
        state.applyAll(commands);
        
        // 4. 질문 맥락 업데이트
        if (interpretation.getPrimaryIntent() == AnswerIntent.ANSWER) {
            if (questionContext.getLastAskedSlot() != null) {
                questionContext.markAnswered();
            }
        }
    }
}