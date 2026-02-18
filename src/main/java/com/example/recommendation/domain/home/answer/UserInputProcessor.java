package com.example.recommendation.domain.home.answer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 사용자 입력 → 슬롯 반영 통합 서비스 (STEP 10)
 *
 * [역할]
 * - 전체 파이프라인 조율
 * - OpenAiAnswerInterpretationService → SlotBindingPolicy → SlotState
 *
 * [절대 금지]
 * - 판단 ❌ (정책에 위임)
 * - AI 호출 ❌ (OpenAiAnswerInterpretationService가 처리)
 */
@Service
public class UserInputProcessor {
    
    private static final Logger log =
            LoggerFactory.getLogger(UserInputProcessor.class);
    
    private final OpenAiAnswerInterpretationService interpretationService;
    private final SlotBindingPolicy bindingPolicy;
    
    public UserInputProcessor(
            OpenAiAnswerInterpretationService interpretationService,
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
        
    	System.out.println("UIP_STATE_HASH=" + state.hashCode());

        log.info("[UserInputProcessor] input: {}", userInput);

        // 🔥 세션 상태 객체 해시 확인
        log.info("🔥 STATE_HASH (UserInputProcessor) = {}", state.hashCode());
        
        // 1. 발화 의도 분류 (AI 기반)
        PendingQuestionContext questionContext = state.getQuestionContext();
        DecisionSlot lastAskedSlot = questionContext.getLastAskedSlot();

        // 🔥 마지막 질문 슬롯 확인
        log.info("🔥 LAST_ASKED_SLOT = {}", lastAskedSlot);
        
        AnswerInterpretation interpretation =
                interpretationService.interpret(userInput, lastAskedSlot);
        
        log.info("[UserInputProcessor] intent: {}, value: {}",
                interpretation.getPrimaryIntent(),
                interpretation.getNormalizedValue());
        
        // 2. 슬롯 귀속 결정
        List<SlotUpdateCommand> commands =
                bindingPolicy.decide(interpretation, questionContext, state);
        
        log.info("[UserInputProcessor] commands: {}", commands.size());

        // 🔥 명령 상세 확인
        log.info("🔥 COMMAND_LIST = {}", commands);
        
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
