package com.example.recommendation.domain.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.ConversationPhase;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.decision.DecisionType;
import com.example.recommendation.domain.home.ai.DiscoveryQuestionAI;
import com.example.recommendation.domain.home.ai.GuideSuggestionAI;
import com.example.recommendation.domain.home.ai.SlotToKeywordAI;
import com.example.recommendation.domain.home.policy.ReadyConditionPolicy;
import com.example.recommendation.domain.home.policy.SlotSelectionPolicy;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;
import com.example.recommendation.dto.RecommendationResponseDto;


@Service
public class HomeService {

    private static final Logger log =
            LoggerFactory.getLogger(HomeService.class);

    private final HomeExplanationService explanationService;
    private final SlotSelectionPolicy slotSelectionPolicy;
    private final DiscoveryQuestionAI discoveryQuestionAI;
    private final GuideSuggestionAI guideSuggestionAI;
    private final SlotToKeywordAI slotToKeywordAI;
    private final ReadyConditionPolicy readyConditionPolicy;
    private final SlotConfirmationService slotConfirmationService;
    private final CriteriaMergeService criteriaMergeService;

    public HomeService(
            HomeExplanationService explanationService,
            SlotSelectionPolicy slotSelectionPolicy,
            DiscoveryQuestionAI discoveryQuestionAI,
            GuideSuggestionAI guideSuggestionAI,
            SlotToKeywordAI slotToKeywordAI,
            ReadyConditionPolicy readyConditionPolicy,
            SlotConfirmationService slotConfirmationService,
            CriteriaMergeService criteriaMergeService
    ) {
        this.explanationService = explanationService;
        this.slotSelectionPolicy = slotSelectionPolicy;
        this.discoveryQuestionAI = discoveryQuestionAI;
        this.guideSuggestionAI = guideSuggestionAI;
        this.slotToKeywordAI = slotToKeywordAI;
        this.readyConditionPolicy = readyConditionPolicy;
        this.slotConfirmationService = slotConfirmationService;
        this.criteriaMergeService = criteriaMergeService;
    }

    public RecommendationResponseDto handle(
            DecisionResult decisionResult,
            RecommendationCriteria criteria,
            HomeConversationState conversationState   // 🔥 이제 파라미터로 받는다
    ) {

        System.out.println("HOME_STATE_HASH=" + conversationState.hashCode());

        DecisionType decisionType = decisionResult.getDecision().getType();
        ConversationPhase phase = decisionResult.getNextPhase();
        HomeReason reason = decisionResult.getHomeReason();

        log.info(
            "[HomeService] decisionType={}, phase={}, reason={}",
            decisionType,
            phase,
            reason
        );

        /* ========================= */
        /* 1️⃣ INVALID               */
        /* ========================= */
        if (decisionType == DecisionType.INVALID) {
            return RecommendationResponseDto.invalid(
                    "추천 가능한 상품이 없습니다."
            );
        }

        /* ========================= */
        /* 2️⃣ DISCOVERY 단계        */
        /* ========================= */
        if (phase == ConversationPhase.DISCOVERY) {

            slotConfirmationService.promoteAnsweredSlots(conversationState);

            if (readyConditionPolicy.isReady(conversationState)) {

                log.info("[HomeService] ✅ READY 상태 진입");

                RecommendationCriteria merged =
                        criteriaMergeService.merge(criteria, conversationState);

                if (merged.getSearchKeyword() == null) {

                    String generatedKeyword =
                            slotToKeywordAI.generate(conversationState);

                    if (generatedKeyword != null &&
                        !generatedKeyword.isBlank()) {

                        merged.setSearchKeyword(generatedKeyword);
                    }
                }

                return RecommendationResponseDto.searchReady(merged);
            }

            DecisionSlot guideSlot =
                    slotSelectionPolicy.selectGuideTarget(conversationState);

            if (guideSlot != null) {

                conversationState
                        .getQuestionContext()
                        .markGuided(guideSlot);

                String guide =
                        guideSuggestionAI.generateSuggestion(
                                guideSlot,
                                conversationState
                        );

                return RecommendationResponseDto.requery(guide);
            }

            DecisionSlot questionSlot =
                    slotSelectionPolicy.selectNext(conversationState);

            if (questionSlot != null) {

                conversationState
                        .getSlot(questionSlot)
                        .markAsked();

                conversationState
                        .getQuestionContext()
                        .markAsked(questionSlot);

                String question =
                        discoveryQuestionAI.generateQuestion(
                                questionSlot,
                                conversationState
                        );

                return RecommendationResponseDto.requery(question);
            }

            return RecommendationResponseDto.requery(
                    explanationService.generateRequery(
                            HomeReason.NEED_MORE_CONDITION,
                            criteria
                    )
            );
        }

        /* ========================= */
        /* 3️⃣ READY 단계 (안전망)    */
        /* ========================= */
        if (phase == ConversationPhase.READY) {

            RecommendationCriteria merged =
                    criteriaMergeService.merge(criteria, conversationState);

            return RecommendationResponseDto.searchReady(merged);
        }

        return RecommendationResponseDto.requery(
                explanationService.generateRequery(
                        HomeReason.NEED_MORE_CONDITION,
                        criteria
                )
        );
    }
}
