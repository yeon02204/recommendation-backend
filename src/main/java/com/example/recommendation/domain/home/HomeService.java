package com.example.recommendation.domain.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.ConversationPhase;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.decision.DecisionType;
import com.example.recommendation.domain.home.ai.DiscoveryQuestionAI;
import com.example.recommendation.domain.home.policy.SlotSelectionPolicy;
import com.example.recommendation.domain.home.slot.DecisionSlot;
import com.example.recommendation.domain.home.state.HomeConversationState;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.domain.home.policy.ReadyConditionPolicy;
import com.example.recommendation.domain.home.ai.GuideSuggestionAI;



/**
 * HOME 단계 전용 서비스
 *
 * [역할]
 * - DecisionResult + 대화 상태를 해석하여
 *   HOME 단계 흐름을 조율하는 진입 서비스
 *
 * [절대 금지]
 * - 판단 ❌
 * - 검색 ❌
 * - 직접 문장 생성 ❌
 */
@Service
public class HomeService {

    private static final Logger log =
            LoggerFactory.getLogger(HomeService.class);

    private final HomeExplanationService explanationService;
    private final SlotSelectionPolicy slotSelectionPolicy;
    private final DiscoveryQuestionAI discoveryQuestionAI;
    private final HomeConversationState conversationState;
    private final ReadyConditionPolicy readyConditionPolicy;
    private final GuideSuggestionAI guideSuggestionAI;
    private final SlotConfirmationService slotConfirmationService;
    private final CriteriaMergeService criteriaMergeService;



    public HomeService(
            HomeExplanationService explanationService,
            SlotSelectionPolicy slotSelectionPolicy,
            DiscoveryQuestionAI discoveryQuestionAI,
            GuideSuggestionAI guideSuggestionAI,
            HomeConversationState conversationState,
            ReadyConditionPolicy readyConditionPolicy,
            SlotConfirmationService slotConfirmationService,
            CriteriaMergeService criteriaMergeService
    ) {
        this.explanationService = explanationService;
        this.slotSelectionPolicy = slotSelectionPolicy;
        this.discoveryQuestionAI = discoveryQuestionAI;
        this.guideSuggestionAI = guideSuggestionAI;
        this.conversationState = conversationState;
        this.readyConditionPolicy = readyConditionPolicy;
        this.slotConfirmationService = slotConfirmationService;
        this.criteriaMergeService = criteriaMergeService;
    }



    public RecommendationResponseDto handle(
            DecisionResult decisionResult,
            RecommendationCriteria criteria
    ) {

        DecisionType decisionType =
                decisionResult.getDecision().getType();
        ConversationPhase phase =
                decisionResult.getNextPhase();
        HomeReason reason =
                decisionResult.getHomeReason();

        log.info(
            "[HomeService] decisionType={}, phase={}, reason={}",
            decisionType,
            phase,
            reason
        );

        /* =========================
         * 1️⃣ 추천 불가
         * ========================= */
        if (decisionType == DecisionType.INVALID) {
            return RecommendationResponseDto.invalid(
                    "추천 가능한 상품이 없습니다."
            );
        }

        /* =========================
         * 2️⃣ DISCOVERY 단계
         * ========================= */
        if (phase == ConversationPhase.DISCOVERY) {

            // 🔥 STEP 9: ANSWERED → CONFIRMED 승격
            slotConfirmationService.promoteAnsweredSlots(conversationState);

            // 0️⃣ READY 판정 (최우선)
            if (readyConditionPolicy.isReady(conversationState)) {
                
                // 🔥 FINAL STEP: CONFIRMED 슬롯 병합
                RecommendationCriteria merged =
                        criteriaMergeService.merge(criteria, conversationState);
                
                String summary =
                        explanationService.generateReadySummary(merged);
                
                return RecommendationResponseDto.requery(summary);
            }

            // 1️⃣ GUIDE 대상 (USER_UNKNOWN)
            DecisionSlot guideSlot =
                    slotSelectionPolicy.selectGuideTarget(conversationState);

            if (guideSlot != null) {
                log.info("[HomeService] DISCOVERY → GUIDE slot={}", guideSlot);

                return RecommendationResponseDto.requery(
                        guideSuggestionAI.generateSuggestion(
                                guideSlot,
                                conversationState
                        )
                );
            }

            // 2️⃣ QUESTION 대상 (EMPTY)
            DecisionSlot questionSlot =
                    slotSelectionPolicy.selectNext(conversationState);

            if (questionSlot != null) {
                log.info("[HomeService] DISCOVERY → QUESTION slot={}", questionSlot);

                // 🔥 STEP 10: 슬롯 ASKED 마킹 + 질문 맥락 추적
                conversationState
                        .getSlot(questionSlot)
                        .markAsked();
                
                conversationState
                        .getQuestionContext()
                        .markAsked(questionSlot);

                return RecommendationResponseDto.requery(
                        discoveryQuestionAI.generateQuestion(
                                questionSlot,
                                conversationState
                        )
                );
            }

            // 3️⃣ fallback
            return RecommendationResponseDto.requery(
                    explanationService.generateRequery(
                            HomeReason.NEED_MORE_CONDITION,
                            criteria
                    )
            );
        }




        /* =========================
         * 3️⃣ READY 단계 (검색 직전 요약)
         * ========================= */
        if (phase == ConversationPhase.READY) {

            String summary =
                    explanationService.generateReadySummary(
                            criteria
                    );

            return RecommendationResponseDto.requery(summary);
        }

        /* =========================
         * 4️⃣ 안전망
         * ========================= */
        String fallback =
                explanationService.generateRequery(
                        HomeReason.NEED_MORE_CONDITION,
                        criteria
                );

        return RecommendationResponseDto.requery(fallback);
    }
}