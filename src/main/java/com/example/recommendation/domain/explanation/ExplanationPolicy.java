package com.example.recommendation.domain.explanation;

/**
 * Decision 결과를 설명 단계로 전달하기 위한 "설명 정책"
 *
 * - 의미만 표현한다
 * - 문자열은 여기서만 매핑한다
 * - ExplanationService는 getMessage()만 호출
 */

/**
 * MVP 정책 고정용 enum.
 *
 * - 문구 실험 / A-B 테스트
 * - 다국어 대응
 * - 톤 조절
 *
 * ❗ 위 항목들은 고도화 단계에서만 수행
 * ❗ MVP 단계에서는 정책 의미만 유지한다
 */

public enum ExplanationPolicy {

    REQUERY_MAINKEYWORD_MISSING,
    REQUERY_NEED_MORE_CONDITION,
    REQUERY_PREFERRED_BRAND,
    REQUERY_SEARCH_ZERO,
    REQUERY_FILTER_ZERO,

    RECOMMEND_CONFIDENT,
    INVALID_NO_RESULT;

    /**
     * 사용자에게 노출될 고정 설명 문장
     * - 정책 ↔ 문장 매핑은 여기서 단일 책임으로 관리
     */
    public String getMessage() {
        switch (this) {
            case REQUERY_MAINKEYWORD_MISSING:
                return "어떤 종류의 상품을 찾고 계신가요?\n예: 헤드셋, 노트북, 선물용 아이템 등";

            case REQUERY_NEED_MORE_CONDITION:
                return "조금 더 구체적인 조건이 있다면 알려주세요.\n예: 사용 목적, 필요한 기능 등";

            case REQUERY_PREFERRED_BRAND:
                return "선호하는 브랜드가 있다면 함께 알려주세요.";

            case REQUERY_SEARCH_ZERO:
                return "조건에 맞는 상품을 찾지 못했습니다.\n다른 조건으로 다시 시도해볼까요?";

            case REQUERY_FILTER_ZERO:
                return "조건이 다소 까다로운 것 같습니다.\n일부 조건을 완화해보시겠어요?";

            case RECOMMEND_CONFIDENT:
                return "조건에 맞는 상품을 추천드릴게요.";

            case INVALID_NO_RESULT:
                return "추천 가능한 상품이 없습니다.";

            default:
                throw new IllegalStateException("Unhandled ExplanationPolicy: " + this);
        }
    }
}
