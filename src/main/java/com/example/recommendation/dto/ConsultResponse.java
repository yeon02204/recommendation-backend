package com.example.recommendation.dto;

import java.util.List;

/**
 * CONSULT 결과 DTO
 * - 질문 응답
 * - 재검색 지시
 */
public class ConsultResponse {

    private final ConsultActionType actionType;
    private final String message;
    private final List<String> questions;

    public ConsultResponse(
            ConsultActionType actionType,
            String message,
            List<String> questions
    ) {
        this.actionType = actionType;
        this.message = message;
        this.questions = questions;
    }

    public ConsultActionType getActionType() {
        return actionType;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getQuestions() {
        return questions;
    }
}
