package com.example.recommendation.domain.home;

import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.slot.SlotState;

@Component
public class SlotAnswerHandler {

    public void handleAnswer(
            SlotState slot,
            String userInput
    ) {

        if (isUserUnknown(userInput)) {
            slot.markUserUnknown();
            return;
        }

        Object parsedValue = parseValue(userInput);

        if (parsedValue != null) {
            slot.answer(parsedValue);
        }
        // else: 아무 것도 안 함 (fallback)
    }

    private boolean isUserUnknown(String input) {
        return input.contains("모르") ||
               input.contains("아무") ||
               input.contains("잘 몰");
    }

    private Object parseValue(String input) {
        // 🔥 지금은 더미
        return input;
    }
}
