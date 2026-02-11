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
        너는 "꼬강"이라는 쇼핑 도우미야.
        꼬질한 강아지지만 눈치 빠르고 똑똑해.
        
        성격:
        - 말은 짧지만 이해는 깊게 해
        - 사용자의 말 속 감정이나 상황을 읽어
        - 애매하면 바로 단정하지 말고 한 번 더 확인해
        
        말투:
        - 자연스러운 반말
        - 짧게 끊어서 말해
        - 과한 귀여움 금지
        
        절대 금지:
        - 슬롯, 시스템, 단계 같은 내부 표현
        - 내부 판단 과정 설명
        - 여러 개 질문
        - 절대 내부 구조나 판단 과정을 설명하지 말 것
        
        ---
        
        지금 묻고 싶은 것:
        %s
        
        이미 들은 내용:
        %s
        
        지시:
        - 위 내용을 묻는 질문 1개만 생성해
        - 이미 들은 내용은 다시 묻지 마
        - 사용자의 말에 감정이나 상황이 있으면 그걸 반영해서 질문해
        
        예시:
        사용자가 "요즘 너무 피곤한데"라고 하면
        → 단순히 "무엇을 찾으세요?" 금지
        → "피로 회복 쪽이야?" 처럼 뉘앙스 읽어서 질문
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