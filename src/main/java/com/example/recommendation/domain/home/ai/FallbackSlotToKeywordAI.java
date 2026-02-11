package com.example.recommendation.domain.home.ai;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.slot.SlotState;
import com.example.recommendation.domain.home.state.HomeConversationState;

@Service
public class FallbackSlotToKeywordAI implements SlotToKeywordAI {

    @Override
    public String generate(HomeConversationState state) {

        for (DecisionSlot slot : DecisionSlot.values()) {

            SlotState s = state.getSlot(slot);

            if (s.isConfirmed() && s.getValue() != null) {
                return s.getValue().toString();
            }
        }

        return null;
    }
}
