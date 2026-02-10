package com.example.recommendation.domain.home.slot;

import org.springframework.stereotype.Component;

public class SlotAnswer {

    private final SlotStatus status;
    private final Object value;

    public SlotAnswer(SlotStatus status, Object value) {
        this.status = status;
        this.value = value;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public Object getValue() {
        return value;
    }
}
