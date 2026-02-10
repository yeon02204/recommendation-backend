package com.example.recommendation.domain.home;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.ai.ReadySummaryAI;
import com.example.recommendation.domain.home.state.HomeConversationState;

@Service
public class ReadySummaryService {

    private final ReadySummaryAI readySummaryAI;

    public ReadySummaryService(
            ReadySummaryAI readySummaryAI
    ) {
        this.readySummaryAI = readySummaryAI;
    }

    public String summarize(HomeConversationState state) {
        return readySummaryAI.summarize(state);
    }
}

