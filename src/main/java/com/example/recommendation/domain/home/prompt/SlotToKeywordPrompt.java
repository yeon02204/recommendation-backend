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
        너는 대화 슬롯을 "검색 가능한 상품 키워드"로 변환하는 전문가다.

        목표:
        - 네이버 쇼핑에서 검색 가능한 카테고리 1개만 생성하라.
        - 검색 결과가 의미 있게 나올 수 있는 키워드만 선택하라.

        ---

        [입력 슬롯]

        - TARGET: %s
        - PURPOSE: %s
        - CONSTRAINT: %s
        - PREFERENCE: %s
        - CONTEXT: %s

        ---

        [우선순위 규칙]

        1순위: CONSTRAINT (가장 구체적)
          - 예: "주방가전" → "주방가전"
          - 예: "커피머신" → "커피머신"

        2순위: PREFERENCE (선호 사항)
          - 예: "무선" → "무선 이어폰" (다른 슬롯 조합)
          - 예: "가벼운" → "가벼운 노트북" (다른 슬롯 조합)

        3순위: PURPOSE (용도)
          - 예: "결혼축하" → "결혼 선물"
          - 예: "생일" → "생일 선물"
          - 예: "집들이" → "집들이 선물"

        4순위: TARGET + PURPOSE 조합
          - 예: TARGET="친구" + PURPOSE="결혼축하" → "결혼 선물"

        ---

        [절대 금지]

        ❌ 너무 추상적인 단어
          - "선물", "추천", "상품" (단독 사용 금지)

        ❌ 대상만 포함
          - "친구", "부모님", "남자친구" (검색 불가능)

        ❌ 검색 결과가 없는 조합
          - "친구 선물 주방가전" (너무 복잡)

        ---

        [출력 예시]

        좋은 예:
        - "결혼 선물"
        - "주방가전"
        - "무선 이어폰"
        - "집들이 선물"
        - "생일 선물"

        나쁜 예:
        - "선물" (너무 넓음)
        - "친구 선물" (대상 포함하면 검색 안됨)
        - "좋은 상품" (추상적)
        - "추천" (의미 없음)

        ---

        [출력 형식]

        반드시 JSON 형식으로만 응답하라.
        다른 설명, 주석, 코멘트 금지.

        {
          "searchKeyword": "키워드"
        }

        ---

        지시:
        - 위 슬롯 정보를 바탕으로 검색 가능한 카테고리 1개를 생성하라
        - 우선순위를 따라 가장 구체적인 것부터 선택하라
        - JSON만 출력하라
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