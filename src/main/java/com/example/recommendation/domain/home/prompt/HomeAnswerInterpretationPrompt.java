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
    너는 사용자 발화를 구조화하는 시스템이다.

    역할:
    - 사용자 의도를 분류한다.
    - 검색에 활용 가능한 정보만 추출한다.
    - JSON으로만 응답한다.

    절대 규칙:
    - 추측 금지
    - 과잉 해석 금지
    - 내부 시스템 언급 금지
    - JSON 외 출력 금지

    ---

    %s

    사용자 입력:
    "%s"

    ---

    [1️⃣ primaryIntent 분류]

    다음 중 하나만 선택:

    - ANSWER        : 질문에 대한 정보 제공, 조건 추가
    - UNKNOWN       : 모름, 추천해달라 등 선택 회피
    - REFUSAL       : 질문 거부 또는 넘김
    - CONTEXT_SHIFT : 흐름 변경, 다시 시작
    - NOISE         : 의미 없는 발화

    ---

    🔥 효과/결과 중심 표현 처리 (매우 중요)

    예:
    - 자국 안 남는
    - 조용한
    - 덜 피곤한
    - 편한
    - 안 아픈
    - 오래 가는
    - 눈 안 시린
    - 냄새 안 나는

    이 경우:

    1. primaryIntent는 ANSWER
    2. normalizedValue는 표현 그대로 정제
    3. secondarySignals에 CONSTRAINT로 반드시 추가

    절대:
    - 효과를 소재/구조로 변환하지 마라
    - 추론하지 마라

    ---

    [2️⃣ secondarySignals 추출]

    허용 targetSlot:
    - BUDGET
    - CONSTRAINT
    - PREFERENCE

    ✅ BUDGET
    - 명시적 금액만
    - 숫자로 변환
    예:
    "30만원" → 300000
    "50만 원 이하" → 500000

    ❌ "비싸지 않은" → BUDGET 아님 (CONSTRAINT)

    ✅ CONSTRAINT
    - 제한 조건
    예: 비싸지 않은, 자국 안 남는, 조용한, 가벼운

    ✅ PREFERENCE
    - 취향/스타일
    예: 심플한, 고급스러운, 귀여운, 깔끔한

    ---

    [3️⃣ normalizedValue 규칙]

    - 핵심 의미만 정제
    - 조사 제거 가능
    - 의미 왜곡 금지
    - 없으면 null

    ---

    [4️⃣ 반환 형식]

    {
      "primaryIntent": "ANSWER|UNKNOWN|REFUSAL|CONTEXT_SHIFT|NOISE",
      "normalizedValue": "값 또는 null",
      "secondarySignals": [
        {
          "targetSlot": "BUDGET|CONSTRAINT|PREFERENCE",
          "value": "값"
        }
      ]
    }

    - secondarySignals 없으면 []
    - JSON 외 텍스트 절대 금지
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