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
import com.example.recommendation.domain.decision.Decision;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.evaluation.EvaluationResult;
import com.example.recommendation.domain.home.HomeService;
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

    public HomeRecommendationOrchestrator(
            CriteriaService criteriaService,
            ConversationContextService contextService,
            SearchReadinessEvaluator searchReadinessEvaluator,
            SearchService searchService,
            RecommendationService recommendationService,
            HomeService homeService,
            RecommendationResponseAssembler assembler
    ) {
        this.criteriaService = criteriaService;
        this.contextService = contextService;
        this.searchReadinessEvaluator = searchReadinessEvaluator;
        this.searchService = searchService;
        this.recommendationService = recommendationService;
        this.homeService = homeService;
        this.assembler = assembler;
    }

    public RecommendationResponseDto handle(RecommendationRequestDto request) {

        if (request == null || request.getUserInput() == null) {
            return RecommendationResponseDto.invalid("요청이 올바르지 않습니다.");
        }

        log.info("[Orchestrator] handle start");

        RecommendationCriteria incoming =
                criteriaService.createCriteria(request.getUserInput());

        CommandType command = incoming.getCommandType();

        if (command == CommandType.RESET) {
            contextService.reset();
        }

        if (command == CommandType.RETRY_SEARCH) {
            return handleRetrySearch();
        }

        contextService.merge(incoming);
        ConversationContext context = contextService.getContext();

        SearchReadiness readiness =
                searchReadinessEvaluator.evaluate(context, incoming);

        if (readiness == SearchReadiness.NEED_MORE_CONTEXT) {
            return homeService.handle(
                    DecisionResult.discovery(
                            Decision.requery(
                                    com.example.recommendation.domain.explanation
                                            .ExplanationPolicy
                                            .REQUERY_NEED_MORE_CONDITION
                            )
                    ),
                    incoming
            );
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
                        products,
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
                        products,
                        cardExplanations
                );

        return RecommendationResponseDto.recommend(
                items,
                message
        );
    }
}
