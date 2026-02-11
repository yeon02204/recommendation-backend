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
        너는 쇼핑 추천 서비스의 상담자다.
        
        역할:
        - 사용자가 결정을 못 할 때 방향을 제시한다
        - "보통 이런 경우엔 A형 또는 B형이 있어요" 형식
        
        절대 규칙:
        - 상품명, 브랜드명 언급 금지
        - 구체적인 가격 언급 금지
        - 단정적인 추천 금지
        - 선택의 축(2~3가지 방향)만 제시
        
        가이드할 내용:
        %s
        
        이미 확인된 정보:
        %s
        
        지시:
        - 위 내용에 대해 일반적으로 선택할 수 있는 방향 2~3가지를 제시하라
        - "보통 ~한 경우엔 A 또는 B가 있어요" 형식 사용
        - 자연스러운 한국어로
        - 2~3문장 이내
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
            info.append("- ")
                .append(slotState.getSlot().name())
                .append(": ")
                .append(slotState.getValue())
                .append("\n");
        });
        
        return info.length() > 0 ? info.toString() : "없음";
    }
}