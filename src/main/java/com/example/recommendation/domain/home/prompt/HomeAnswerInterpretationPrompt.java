package com.example.recommendation.domain.home.prompt;

import com.example.recommendation.domain.home.slot.DecisionSlot;

/**
 * 답변 해석 프롬프트
 *
 * [역할]
 * - 사용자 발화 → 의도 분류 + 정보 추출
 * - 구조화된 JSON 반환
 *
 * [절대 금지]
 * - 판단 ❌
 * - 추천 ❌
 */
public class HomeAnswerInterpretationPrompt {
    
    private final String userInput;
    private final DecisionSlot lastAskedSlot;
    
    public HomeAnswerInterpretationPrompt(
            String userInput,
            DecisionSlot lastAskedSlot
    ) {
        this.userInput = userInput;
        this.lastAskedSlot = lastAskedSlot;
    }
    
    /**
     * AI에게 전달할 답변 해석 프롬프트
     */
    public String toPromptText() {
        
        String lastQuestion = lastAskedSlot != null 
                ? "마지막 질문: " + getSlotQuestion(lastAskedSlot)
                : "질문 없음 (자유 발화)";
        
        return """
        너는 사용자 발화를 분석하는 전문가다.
        
        역할:
        - 사용자 답변의 의도를 분류한다
        - 정보를 추출한다
        - JSON 형식으로 반환한다
        
        %s
        
        사용자 입력:
        "%s"
        
        분류 기준:
        1. ANSWER: 실제 답변 (예: "친한 친구요", "30만원 정도요")
        2. UNKNOWN: 모름 표현 (예: "모르겠어요", "잘 모르겠는데요", "아무거나")
        3. REFUSAL: 거부 (예: "넘어갈게요", "상관없어요")
        4. CONTEXT_SHIFT: 화제 전환 (예: "다른 거 추천해줘", "처음부터 다시")
        5. NOISE: 의미 없음 (예: "음...", "그냥")
        
        추가 정보 추출:
        - 예산이 언급되면 숫자 추출 (예: "30만원" → 300000)
        - 제약 조건 (예: "비싸지 않은", "튀지 않는")
        - 선호 사항 (예: "심플한", "고급스러운")
        
        반환 형식 (JSON):
        {
          "primaryIntent": "ANSWER|UNKNOWN|REFUSAL|CONTEXT_SHIFT|NOISE",
          "normalizedValue": "정제된 값",
          "secondarySignals": [
            {
              "targetSlot": "BUDGET|CONSTRAINT|PREFERENCE",
              "value": "추출된 값"
            }
          ]
        }
        
        주의사항:
        - secondarySignals는 명시적으로 언급된 것만
        - 추측하지 마라
        - JSON만 반환하라 (다른 설명 금지)
        """.formatted(lastQuestion, userInput);
    }
    
    private String getSlotQuestion(DecisionSlot slot) {
        return switch (slot) {
            case TARGET -> "누구를 위한 상품인가요?";
            case PURPOSE -> "어떤 용도로 사용할 예정인가요?";
            case CONSTRAINT -> "피하고 싶은 점이 있을까요?";
            case PREFERENCE -> "선호하는 스타일이나 성향이 있을까요?";
            case BUDGET -> "예산은 어느 정도로 생각하고 계신가요?";
            case CONTEXT -> "어떤 상황에서 쓰실 예정인가요?";
        };
    }
}