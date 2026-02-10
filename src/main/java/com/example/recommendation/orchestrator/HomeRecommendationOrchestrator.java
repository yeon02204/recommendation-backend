package com.example.recommendation.orchestrator;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.criteria.CommandType;
import com.example.recommendation.domain.criteria.ConversationContext;
import com.example.recommendation.domain.criteria.ConversationContextService;
import com.example.recommendation.domain.criteria.CriteriaService;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.criteria.SearchReadiness;
import com.example.recommendation.domain.criteria.SearchReadinessEvaluator;
import com.example.recommendation.domain.criteria.SearchReadinessResult;
import com.example.recommendation.domain.decision.Decision;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.home.HomeService;
import com.example.recommendation.domain.home.answer.UserInputProcessor;
import com.example.recommendation.domain.home.state.HomeConversationState;
import com.example.recommendation.domain.recommendation.RecommendationService;
import com.example.recommendation.domain.search.SearchService;
import com.example.recommendation.dto.RecommendationRequestDto;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.external.naver.dto.Product;

@Component
public class HomeRecommendationOrchestrator {

    private static final Logger log =
            LoggerFactory.getLogger(HomeRecommendationOrchestrator.class);

    private final CriteriaService criteriaService;
    private final ConversationContextService contextService;
    private final SearchReadinessEvaluator searchReadinessEvaluator;
    private final SearchService searchService;
    private final RecommendationService recommendationService;
    private final HomeService homeService;
    private final RecommendationResponseAssembler assembler;
    private final UserInputProcessor userInputProcessor;
    private final HomeConversationState homeConversationState;

    public HomeRecommendationOrchestrator(
            CriteriaService criteriaService,
            ConversationContextService contextService,
            SearchReadinessEvaluator searchReadinessEvaluator,
            SearchService searchService,
            RecommendationService recommendationService,
            HomeService homeService,
            RecommendationResponseAssembler assembler,
            UserInputProcessor userInputProcessor,
            HomeConversationState homeConversationState
    ) {
        this.criteriaService = criteriaService;
        this.contextService = contextService;
        this.searchReadinessEvaluator = searchReadinessEvaluator;
        this.searchService = searchService;
        this.recommendationService = recommendationService;
        this.homeService = homeService;
        this.assembler = assembler;
        this.userInputProcessor = userInputProcessor;
        this.homeConversationState = homeConversationState;
    }

    public RecommendationResponseDto handle(RecommendationRequestDto request) {

        if (request == null || request.getUserInput() == null) {
            return RecommendationResponseDto.invalid("요청이 올바르지 않습니다.");
        }

        log.info("[Orchestrator] handle start");

        /* =========================
         * 1️⃣ 입력 → Criteria
         * ========================= */
        RecommendationCriteria incoming =
                criteriaService.createCriteria(request.getUserInput());

        CommandType command = incoming.getCommandType();

        /* =========================
         * 2️⃣ RESET
         * ========================= */
        if (command == CommandType.RESET) {
            log.info("[Orchestrator] RESET → context reset");
            contextService.reset();
        }

        /* =========================
         * 3️⃣ RETRY_SEARCH
         * ========================= */
        if (command == CommandType.RETRY_SEARCH) {
            return handleRetrySearch();
        }

        /* =========================
         * 4️⃣ Context merge
         * ========================= */
        contextService.merge(incoming);
        ConversationContext context = contextService.getContext();

        /* =========================
         * 5️⃣ 검색 준비도 평가
         * ========================= */
        SearchReadinessResult readinessResult =
                searchReadinessEvaluator.evaluate(context, incoming);

        if (readinessResult.readiness() == SearchReadiness.NEED_MORE_CONTEXT) {

            log.info(
                "[Orchestrator] NEED_MORE_CONTEXT → HOME (reason={})",
                readinessResult.reason()
            );

            // 🔥 STEP 10: 사용자 입력 → HOME 슬롯 반영
            userInputProcessor.processUserInput(
                    request.getUserInput(),
                    homeConversationState
            );

            return homeService.handle(
                    DecisionResult.discovery(
                            Decision.requery(),
                            readinessResult.reason()
                    ),
                    incoming
            );
        }

        /* =========================
         * 6️⃣ 검색용 Criteria 확정
         * ========================= */
        RecommendationCriteria criteriaForSearch =
                context.toCriteria();

        /* =========================
         * 7️⃣ 검색
         * ========================= */
        List<Product> products =
                searchService.search(criteriaForSearch);

        /* =========================
         * 8️⃣ 평가 (합격자 선별)
         * ========================= */
        EvaluationResult evaluationResult =
                recommendationService.evaluate(
                        criteriaForSearch,
                        products
                );

        /* =========================
         * 9️⃣ 메인 메시지
         * ========================= */
        String message =
                assembler.buildMainMessage(
                        evaluationResult,
                        criteriaForSearch
                );

        /* =========================
         * 🔟 카드 설명
         * ========================= */
        Map<Long, String> cardExplanations =
                assembler.buildCardExplanations(
                        evaluationResult,
                        criteriaForSearch
                );

        /* =========================
         * 1️⃣1️⃣ Item 조립 (합격자만)
         * ========================= */
        List<RecommendationResponseDto.Item> items =
                assembler.assembleItems(
                        evaluationResult,
                        cardExplanations
                );

        return RecommendationResponseDto.recommend(
                items,
                message
        );
    }

    /**
     * 🔁 같은 조건으로 다시 검색
     */
    private RecommendationResponseDto handleRetrySearch() {

        log.info("[Orchestrator] handleRetrySearch start");

        ConversationContext context = contextService.getContext();
        RecommendationCriteria criteria = context.toCriteria();

        int offset = context.getRetryCount() * 5;

        List<Product> products =
                searchService.searchWithOffset(criteria, offset);

        context.increaseRetryCount();

        EvaluationResult evaluationResult =
                recommendationService.evaluate(criteria, products);

        String message =
                assembler.buildMainMessage(
                        evaluationResult,
                        criteria
                );

        Map<Long, String> cardExplanations =
                assembler.buildCardExplanations(
                        evaluationResult,
                        criteria
                );

        List<RecommendationResponseDto.Item> items =
                assembler.assembleItems(
                        evaluationResult,
                        cardExplanations
                );

        return RecommendationResponseDto.recommend(
                items,
                message
        );
    }
}