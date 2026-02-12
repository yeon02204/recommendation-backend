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
        너는 대화 슬롯을 "네이버 쇼핑에서 실제 검색 가능한 상품 키워드"로 변환하는 전문가다.

		목표:
		- 네이버 쇼핑에서 검색 시 의미 있는 결과가 나오는 카테고리 1개만 생성하라.
		- 검색 가능한 단어만 출력하라.
		- JSON 형식으로만 응답하라.
		
		---
		
		[입력 슬롯]
		
		- TARGET: %s
		- PURPOSE: %s
		- CONSTRAINT: %s
		- PREFERENCE: %s
		- CONTEXT: %s
		
		---
		
		🔥 1단계: 효과 표현 감지 (매우 중요)
		
		사용자가 아래와 같은 "효과 / 결과 표현"을 말한 경우:
		
		예:
		- 자국 안 남는
		- 덜 피곤한
		- 조용한
		- 편한
		- 안 아픈
		- 덜 답답한
		- 오래 가는
		- 가벼운
		
		→ 절대 그대로 검색어로 사용하지 마라.
		→ 먼저 쇼핑에서 실제 사용되는 "구체적 요소"로 변환하라.
		
		[효과 표현 → 쇼핑 표준 표현 변환 규칙]
		
		- 조용한 → 저소음
		- 무소음 → 저소음
		- 자국 안 남는 → 매쉬 / 패브릭 / 압박 적은
		- 안 벗겨지는 → 밀착형
		- 덜 답답한 → 통풍 / 매쉬
		- 오래 가는 → 대용량 / 고함량
		- 가벼운 → 경량
		- 안 아픈 → 저자극
		- 피로 회복 → 고함량 / 밀크씨슬 / 비타민B군
		- 눈 안 피곤한 → 블루라이트 차단
		
		⚠️ 효과 표현만 존재할 경우:
		→ 검색 키워드를 생성하지 말고
		→ null 반환하라.
		
		---
		
		🔥 2단계: 우선순위 규칙
		
		1순위: CONSTRAINT (가장 구체적인 것)
		  예: "주방가전" → "주방가전"
		  예: "커피머신" → "커피머신"
		
		2순위: PREFERENCE + 다른 슬롯 조합
		  예: "무선" + 이어폰 관련 맥락 → "무선 이어폰"
		  예: "저소음" + 가습기 → "저소음 가습기"
		
		3순위: PURPOSE 기반 상품화 가능할 때만
		  예: "결혼축하" → "결혼 선물"
		  예: "집들이" → "집들이 선물"
		
		4순위: TARGET + PURPOSE 조합
		  예: TARGET="친구" + PURPOSE="결혼축하"
		      → "결혼 선물"
		
		---
		
		❌ 절대 금지
		
		- "선물" 단독 사용
		- "추천", "상품" 같은 추상어
		- 대상만 포함 ("친구", "부모님")
		- 너무 복잡한 조합 ("친구 결혼 선물 주방가전")
		
		---
		
		🔥 최종 판단 기준
		
		이 키워드 하나로 네이버 쇼핑에 검색했을 때
		상품 목록이 의미 있게 나오는가?
		
		YES → searchKeyword 생성
		NO → null 반환
		
		---
		
		[출력 형식]
		
		JSON으로만 응답하라.
		설명, 주석, 텍스트 금지.
		
		{
		  "searchKeyword": "키워드" | null
		}

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