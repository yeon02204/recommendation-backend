package com.example.recommendation.domain.home.policy;

public enum SlotSufficiency {
    REQUIRED,    // 반드시 필요
    OPTIONAL,    // 있으면 좋음
    FILLED,      // 값 있음
    MISSING      // 아직 없음
}
