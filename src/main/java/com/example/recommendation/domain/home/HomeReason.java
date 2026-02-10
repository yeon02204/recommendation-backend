package com.example.recommendation.domain.home;

// 왜 HOME 단계에 머무는지 나타내는 사유 enum

public enum HomeReason {
    NO_KEYWORD,              // 검색 키워드 없음
    NEED_MORE_CONDITION,     // 키워드는 있으나 조건 부족
    AFTER_RESET,             // 초기화 직후
    AFTER_RETRY,             // 다시보기 직후
    READY_SUMMARY            // 검색 직전 요약 단계
}
