package com.example.recommendation.dto;

/**
 * CONSULT 응답의 의도
 * - ASK_MORE    : 추가 질문
 * - RETRY_SEARCH: 같은 조건으로 재검색
 */
public enum ConsultActionType {
	ASK_MORE,      // 조건이 부족하거나, 의도가 불명확해서 질문 필요
    RETRY_SEARCH  // 조건은 충분 → 같은 조건으로 다시 검색
}
