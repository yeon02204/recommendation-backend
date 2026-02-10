package com.example.recommendation.domain.home.slot;

import java.util.List;

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
