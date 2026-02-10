package com.example.recommendation.domain.home.slot;

import org.springframework.stereotype.Component;

@Component
public class SlotAnswerInterpreter {

    public SlotAnswer interpret(
            DecisionSlot slot,
            String userInput
    ) {

        if (isUnknown(userInput)) {
            return new SlotAnswer(
                    SlotStatus.USER_UNKNOWN,
                    null
            );
        }

        // 🔥 MVP: 일단 답변 받았다고만 처리
        return new SlotAnswer(
                SlotStatus.ANSWERED,
                userInput
        );
    }

    private boolean isUnknown(String input) {
        if (input == null) return true;

        String normalized = input.trim();

        return normalized.isEmpty()
                || normalized.contains("모르")
                || normalized.contains("아무")
                || normalized.contains("잘 몰");
    }
}
