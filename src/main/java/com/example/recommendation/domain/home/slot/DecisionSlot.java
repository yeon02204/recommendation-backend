package com.example.recommendation.domain.home.slot;
// 상담 과정에서 수집해야 할 정보의 종류를 정의한 enum
public enum DecisionSlot {
    TARGET,        // 누구를 위한 것인가
    PURPOSE,       // 왜 쓰는가
    CONSTRAINT,    // 피해야 할 것
    PREFERENCE,    // 선호 성향
    BUDGET,        // 예산
    CONTEXT        // 상황/배경
}
