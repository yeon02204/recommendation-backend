package com.example.recommendation.domain.home.prompt;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 질문 생성 프롬프트
 *
 * [역할]
 * - 특정 슬롯에 대해 질문 1개만 생성
 * - 현재 대화 상태 고려
 *
 * [절대 금지]
 * - 가이드/예시 제공 ❌
 * - 여러 질문 ❌
 * - 판단/추천 ❌
 */
public class HomeQuestionPrompt {
    
    private final DecisionSlot targetSlot;
    private final HomeConversationState state;
    
    public HomeQuestionPrompt(
            DecisionSlot targetSlot,
            HomeConversationState state
    ) {
        this.targetSlot = targetSlot;
        this.state = state;
    }
    
    /**
     * AI에게 전달할 질문 생성 프롬프트
     */
    public String toPromptText() {
        
        String slotDescription = getSlotDescription(targetSlot);
        String confirmedInfo = buildConfirmedInfo();
        
        return """
        너는 쇼핑 추천 서비스의 상담자다.
        
        역할:
        - 사용자에게 질문 1개만 던진다
        - 짧고 명확하게
        
        절대 규칙:
        - 질문은 반드시 1개만
        - 가이드, 예시, 설명 금지
        - 상품명, 브랜드 언급 금지
        - "~은 어떠세요?" 같은 제안 금지
        
        질문할 내용:
        %s
        
        이미 확인된 정보:
        %s
        
        지시:
        - 위 내용을 묻는 자연스러운 질문 1개를 생성하라
        - 이미 확인된 정보는 다시 묻지 마라
        - 존댓말 사용
        """.formatted(slotDescription, confirmedInfo);
    }
    
    private String getSlotDescription(DecisionSlot slot) {
        return switch (slot) {
            case TARGET -> "누구를 위한 상품인지 (예: 본인, 가족, 친구, 동료)";
            case PURPOSE -> "어떤 용도로 사용할 것인지 (예: 선물, 개인 사용, 업무)";
            case CONSTRAINT -> "피하고 싶은 조건이나 제외할 것 (예: 무겁지 않은 것, 소음 없는 것)";
            case PREFERENCE -> "선호하는 스타일이나 취향 (예: 심플한 디자인, 고급스러운 느낌)";
            case BUDGET -> "예산 범위 (예: 10만원 이하, 50만원 정도)";
            case CONTEXT -> "사용 상황이나 환경 (예: 야외 활동, 사무실, 집)";
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