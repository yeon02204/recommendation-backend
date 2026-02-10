package com.example.recommendation.domain.home.slot;

import java.util.List;

// 슬롯 우선순위를 계산하는 규칙 인터페이스

public enum SlotPriority {

    DEFAULT(
        List.of(
            DecisionSlot.TARGET,
            DecisionSlot.PURPOSE,
            DecisionSlot.CONSTRAINT,
            DecisionSlot.PREFERENCE,
            DecisionSlot.BUDGET,
            DecisionSlot.CONTEXT
        )
    );

    private final List<DecisionSlot> order;

    SlotPriority(List<DecisionSlot> order) {
        this.order = order;
    }

    public List<DecisionSlot> getOrder() {
        return order;
    }
}
