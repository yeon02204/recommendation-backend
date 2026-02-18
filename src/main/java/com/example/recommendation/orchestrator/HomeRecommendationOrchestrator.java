package com.example.recommendation.orchestrator;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
import com.example.recommendation.dto.RecommendationResponseDto.ResponseType;
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

        // 🔥 세션ID 로그 확인용 (구조 변경 아님)
        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        String sessionId = attr.getRequest().getSession().getId();
        System.out.println("🔥 세션ID = " + sessionId);

        if (request == null || request.getUserInput() == null) {
            return RecommendationResponseDto.invalid("요청이 올바르지 않습니다.");
        }

        log.info("[Orchestrator] handle start");

        RecommendationCriteria incoming =
                criteriaService.createCriteria(request.getUserInput());

        CommandType command = incoming.getCommandType();

        if (command == CommandType.RESET) {
            log.info("[Orchestrator] RESET → context reset");
            contextService.reset();
        }

        if (command == CommandType.RETRY_SEARCH) {
            return handleRetrySearch();
        }

        contextService.merge(incoming);
        ConversationContext context = contextService.getContext();

        SearchReadinessResult readinessResult =
                searchReadinessEvaluator.evaluate(context, incoming);

        if (readinessResult.readiness() == SearchReadiness.NEED_MORE_CONTEXT) {

            userInputProcessor.processUserInput(
                    request.getUserInput(),
                    homeConversationState
            );

            RecommendationResponseDto homeResponse = homeService.handle(
                    DecisionResult.discovery(
                            Decision.requery(),
                            readinessResult.reason()
                    ),
                    incoming
            );

            if (homeResponse.getType() == ResponseType.SEARCH_READY) {

                RecommendationCriteria criteriaForSearch =
                        homeResponse.getCriteria();

                if (criteriaForSearch == null) {
                    return RecommendationResponseDto.invalid(
                            "검색 조건이 준비되지 않았습니다."
                    );
                }

                List<Product> products =
                        searchService.search(criteriaForSearch);

                EvaluationResult evaluationResult =
                        recommendationService.evaluate(
                                criteriaForSearch,
                                products
                        );

                String message =
                        assembler.buildMainMessage(
                                evaluationResult,
                                criteriaForSearch
                        );

                Map<Long, String> cardExplanations =
                        assembler.buildCardExplanations(
                                evaluationResult,
                                criteriaForSearch
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

            return homeResponse;
        }

        RecommendationCriteria criteriaForSearch =
                context.toCriteria();

        List<Product> products =
                searchService.search(criteriaForSearch);

        EvaluationResult evaluationResult =
                recommendationService.evaluate(
                        criteriaForSearch,
                        products
                );

        String message =
                assembler.buildMainMessage(
                        evaluationResult,
                        criteriaForSearch
                );

        Map<Long, String> cardExplanations =
                assembler.buildCardExplanations(
                        evaluationResult,
                        criteriaForSearch
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

    private RecommendationResponseDto handleRetrySearch() {

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
