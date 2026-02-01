package com.example.recommendation.domain.decision;

public enum ConfidenceState {
    INSUFFICIENT_DATA, // 판단 불가
    WEAK_SIGNAL,       // 애매함
    STRONG_SIGNAL      // 충분함
}
