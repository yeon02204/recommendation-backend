package com.example.recommendation.domain.home.prompt;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * SlotToKeyword 전용 프롬프트 모델
 *
 * [역할]
 * - CONFIRMED 슬롯을 검색 가능한 키워드로 변환하기 위한 프롬프트 생성
 *
 * [절대 금지]
 * - 판단 ❌
 * - 추천 ❌
 * - 상품명 생성 ❌
 */
public class SlotToKeywordPrompt {

    private final String target;
    private final String purpose;
    private final String constraint;
    private final String preference;
    private final String context;

    public SlotToKeywordPrompt(HomeConversationState state) {
        this.target = getConfirmedValue(state, DecisionSlot.TARGET);
        this.purpose = getConfirmedValue(state, DecisionSlot.PURPOSE);
        this.constraint = getConfirmedValue(state, DecisionSlot.CONSTRAINT);
        this.preference = getConfirmedValue(state, DecisionSlot.PREFERENCE);
        this.context = getConfirmedValue(state, DecisionSlot.CONTEXT);
    }

    /**
     * AI에게 전달할 SlotToKeyword 프롬프트
     */
    public String toPromptText() {

        return """
	    너는 대화 슬롯을
	    네이버 쇼핑에서 검색 가능한 상품 키워드 1개로 변환하는 역할만 수행한다.
	
	    역할:
	    - 검색 가능한 단어 1개만 생성한다.
	    - 판단하지 않는다.
	    - 추천하지 않는다.
	    - JSON으로만 응답한다.
	
	    ---
	
	    [입력 슬롯]
	
	    - TARGET: %s
	    - PURPOSE: %s
	    - CONSTRAINT: %s
	    - PREFERENCE: %s
	    - CONTEXT: %s
	
	    ---
	
	    [생성 규칙]
	
	    1️⃣ searchKeyword는 반드시
	    "네이버 쇼핑에서 단독 검색 가능한 상품명 또는 카테고리"여야 한다.
	
	    예:
	    노트북, 가습기, 수영모, 커피머신, 무선이어폰
	
	    2️⃣ 효과 표현은 그대로 쓰지 말고
	    이미 명확한 상품이 있을 때만 결합하라.
	
	    예:
	    CONSTRAINT="저소음"
	    가습기 맥락 존재 → "저소음 가습기"
	
	    ❌ 효과 표현만 존재하면 searchKeyword 생성 금지
	    → null 반환
	
	    3️⃣ 우선순위
	
	    1순위: 이미 상품명에 가까운 CONSTRAINT
	    2순위: PREFERENCE + 명확한 상품 맥락
	    3순위: PURPOSE가 상품화 가능한 경우 ("집들이 선물")
	    4순위: TARGET 단독 사용 금지
	
	    4️⃣ 절대 금지
	
	    - "선물" 단독
	    - "추천", "상품", "아이템"
	    - 대상만 포함 ("친구", "부모님")
	    - 과도한 조합
	
	    ---
	
	    최종 기준:
	
	    이 단어 하나로 네이버 쇼핑에서
	    상품 목록이 명확히 나오는가?
	
	    YES → searchKeyword 생성
	    NO → null
	
	    ---
	
	    [출력 형식]
	
	    {
	      "searchKeyword": "키워드" | null
	    }
	
	    JSON 외 텍스트 출력 금지.
	    """.formatted(
	                nvl(target),
	                nvl(purpose),
	                nvl(constraint),
	                nvl(preference),
	                nvl(context)
	        );
	    }


    private String getConfirmedValue(HomeConversationState state, DecisionSlot slot) {

        if (!state.getSlot(slot).isConfirmed()) {
            return null;
        }

        Object value = state.getSlot(slot).getValue();

        return value != null ? value.toString() : null;
    }


    private String nvl(String value) {
        return value == null ? "없음" : value;
    }
}