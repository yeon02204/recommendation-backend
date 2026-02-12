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
		- 사용자 답변의 "의도"를 정확히 분류한다.
		- 실제 검색에 활용 가능한 정보만 추출한다.
		- JSON 형식으로만 반환한다.
		
		절대 규칙:
		- 추측 금지
		- 과잉 해석 금지
		- 내부 시스템 구조 언급 금지
		- JSON 외 출력 금지
		
		---
		
		사용자 입력:
		"%s"
		
		---
		
		[1️⃣ 의도 분류 기준 - 매우 엄격하게 판단]
		
		primaryIntent는 아래 중 하나만 선택한다.
		
		1. ANSWER
		   - 질문에 대한 직접적인 정보 제공
		   - 조건 추가
		   - 구체화
		   예:
		   "친한 친구요"
		   "30만원 이하"
		   "무선으로"
		   "심플한 걸로"
		
		2. UNKNOWN
		   - 모름 / 선택 어려움
		   예:
		   "모르겠어요"
		   "아무거나"
		   "추천해줘"
		   "잘 모르겠는데"
		
		3. REFUSAL
		   - 질문을 회피하거나 거부
		   예:
		   "그건 상관없어요"
		   "넘어갈게요"
		   "그건 안 정할래요"
		
		4. CONTEXT_SHIFT
		   - 기존 흐름을 중단하거나 새로 시작하려는 의도
		   예:
		   "다른 거 추천해줘"
		   "처음부터 다시"
		   "그게 아니라"
		   "아니 이거 말고"
		
		5. NOISE
		   - 의미 없는 발화
		   예:
		   "음..."
		   "그냥"
		   "뭐지"
		   "ㅎㅎ"
		
		---
		
		🔥 매우 중요 - 효과 중심 표현 처리
		
		사용자가 아래와 같은 "결과/효과 중심 표현"을 말한 경우:
		
		예:
		- 자국 안 남는
		- 덜 피곤한
		- 조용한
		- 편한
		- 안 아픈
		- 오래 가는
		- 눈 안 시린
		- 냄새 안 나는
		
		이 경우:
		
		1️⃣ primaryIntent는 ANSWER로 분류한다.
		2️⃣ normalizedValue에는 원문을 그대로 정제하여 넣는다.
		3️⃣ secondarySignals에 CONSTRAINT로 반드시 추가한다.
		
		예:
		"자국 안 남는 수영모"
		→
		{
		  "primaryIntent": "ANSWER",
		  "normalizedValue": "자국 안 남는",
		  "secondarySignals": [
		    {
		      "targetSlot": "CONSTRAINT",
		      "value": "자국 안 남는"
		    }
		  ]
		}
		
		절대:
		- 효과를 소재나 구조로 변환하지 마라
		- 추론해서 다른 값으로 바꾸지 마라
		
		---
		
		[2️⃣ 추가 정보 추출 규칙]
		
		secondarySignals는 명시적으로 언급된 것만 추출한다.
		
		허용 targetSlot:
		
		- BUDGET
		- CONSTRAINT
		- PREFERENCE
		
		---
		
		✅ BUDGET 추출 규칙
		
		- 명시적 금액 표현만 허용
		- 숫자로 변환
		예:
		"30만원" → 300000
		"50만 원 이하" → 500000
		
		❌ 추측 금지
		예:
		"비싸지 않은" → BUDGET로 해석 금지 (CONSTRAINT로 처리)
		
		---
		
		✅ CONSTRAINT 추출
		
		- 제한 조건
		예:
		"비싸지 않은"
		"자국 안 남는"
		"조용한"
		"가벼운"
		
		---
		
		✅ PREFERENCE 추출
		
		- 취향 / 스타일 / 감성 표현
		예:
		"심플한"
		"고급스러운"
		"귀여운"
		"깔끔한"
		
		---
		
		[3️⃣ normalizedValue 규칙]
		
		- 핵심 의미만 남기고 정제
		- 조사 제거 가능
		- 문장 전체가 아닌 핵심 표현만
		- 의미 왜곡 금지
		
		예:
		"친한 친구예요"
		→ "친한 친구"
		
		"30만원 정도 생각하고 있어요"
		→ "30만원"
		
		---
		
		[4️⃣ 반환 형식 - 엄격 준수]
		
		{
		  "primaryIntent": "ANSWER|UNKNOWN|REFUSAL|CONTEXT_SHIFT|NOISE",
		  "normalizedValue": "정제된 값 또는 null",
		  "secondarySignals": [
		    {
		      "targetSlot": "BUDGET|CONSTRAINT|PREFERENCE",
		      "value": "추출된 값"
		    }
		  ]
		}
		
		---
		
		추가 규칙:
		
		- secondarySignals는 배열이며 없으면 [].
		- normalizedValue가 없으면 null.
		- 추측 금지.
		- 사용자 표현을 왜곡하지 마라.
		- JSON 외 텍스트 출력 절대 금지.

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