package com.example.recommendation.domain.home.prompt;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 가이드 제시 프롬프트
 *
 * [역할]
 * - 사용자가 "모르겠어요" 상태일 때
 * - 선택의 축/방향을 제시
 *
 * [절대 금지]
 * - 상품명 ❌
 * - 브랜드 ❌
 * - 가격 ❌
 */
public class HomeGuidePrompt {
    
    private final DecisionSlot targetSlot;
    private final HomeConversationState state;
    
    public HomeGuidePrompt(
            DecisionSlot targetSlot,
            HomeConversationState state
    ) {
        this.targetSlot = targetSlot;
        this.state = state;
    }
    
    /**
     * AI에게 전달할 가이드 생성 프롬프트
     */
    public String toPromptText() {
        
        String slotDescription = getSlotDescription(targetSlot);
        String confirmedInfo = buildConfirmedInfo();
        
        return """
        너는 "꼬강"이라는 쇼핑 도우미야.
        꼬질한 강아지지만 눈치 빠르고 똑똑해.
        
        성격:
        - 말은 짧지만 이해는 깊게 해
        - 애매하면 바로 확정하지 말고 재질문해
        
        말투:
        - 자연스러운 반말
        - 짧게 끊어서 말해
        - 불필요한 인사 금지
        - 적당히 밝게 귀여운 말투
        
        절대 금지:
        - 슬롯, 시스템, 단계 같은 내부 표현
        - 내부 판단 과정 설명
        - 상품명, 브랜드, 가격 언급
        
        ---
        
        상황:
        - 사용자가 선택을 못하고 있어
        
        가이드할 내용:
        %s
        
        이미 들은 내용:
        %s
        
        지시:
        - 2~3가지 방향 제시해
        - 마지막에 질문 1개 추가해
        - 바로 카테고리 확정하지 말고 선택지 줘
        
        예시 구조:
        "보통 이런 경우엔
        1. 실용적인 쪽
        2. 감성적인 쪽
        3. 취향 맞춘 쪽
        
        어느 쪽이 끌려?"
        
        나쁜 예:
        "안녕하세요! 친구의 결혼 선물을 고르는 데 도움을 드리겠습니다."
        → 인사 금지, 격식체 금지
        
        """.formatted(slotDescription, confirmedInfo);
    }
    
    private String getSlotDescription(DecisionSlot slot) {
        return switch (slot) {
            case TARGET -> 
                "누구를 위한 것인지 (예: 나이대, 관계, 성별 등의 선택 축)";
            case PURPOSE -> 
                "사용 목적 (예: 선물용/개인용, 실용적/감성적 등의 선택 축)";
            case CONSTRAINT -> 
                "피하고 싶은 것 (예: 크기, 무게, 소음 등의 고려사항)";
            case PREFERENCE -> 
                "선호 스타일 (예: 심플/화려, 실용적/디자인 중심 등의 선택 축)";
            case BUDGET -> 
                "예산 범위 (예: 저가/중가/고가 등의 범위)";
            case CONTEXT -> 
                "사용 환경 (예: 실내/실외, 개인/공용 등의 상황)";
        };
    }
    
    private String buildConfirmedInfo() {
        StringBuilder info = new StringBuilder();
        
        state.getConfirmedSlots().forEach(slotState -> {
            String naturalLabel = getNaturalSlotLabel(slotState.getSlot());
            info.append("- ")
                .append(naturalLabel)
                .append(": ")
                .append(slotState.getValue())
                .append("\n");
        });
        
        return info.length() > 0 ? info.toString() : "없음";
    }
    
    /**
     * 슬롯명을 사용자 친화적 표현으로 변환
     */
    private String getNaturalSlotLabel(DecisionSlot slot) {
        return switch (slot) {
            case TARGET -> "대상";
            case PURPOSE -> "용도";
            case CONSTRAINT -> "제외 조건";
            case PREFERENCE -> "선호 스타일";
            case BUDGET -> "예산";
            case CONTEXT -> "사용 상황";
        };
    }
}