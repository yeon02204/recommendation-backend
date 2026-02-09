package com.example.recommendation.orchestrator;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.consult.ConsultService;
import com.example.recommendation.domain.criteria.CommandType;
import com.example.recommendation.domain.criteria.ConversationContext;
import com.example.recommendation.domain.criteria.ConversationContextService;
import com.example.recommendation.domain.criteria.CriteriaService;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.criteria.SearchReadiness;
import com.example.recommendation.domain.criteria.SearchReadinessEvaluator;
import com.example.recommendation.domain.decision.Decision;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.explanation.ExplanationPolicy;
import com.example.recommendation.domain.explanation.ExplanationService;
import com.example.recommendation.domain.home.HomeService;
import com.example.recommendation.domain.recommendation.RecommendationService;
import com.example.recommendation.domain.search.SearchService;
import com.example.recommendation.dto.ConsultResponse;
import com.example.recommendation.dto.RecommendationRequestDto;
import com.example.recommendation.dto.RecommendationResponseDto;
import com.example.recommendation.external.naver.Product;

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
    private final ConsultService consultService;
    private final ExplanationService explanationService;

    public HomeRecommendationOrchestrator(
            CriteriaService criteriaService,
            ConversationContextService contextService,
            SearchReadinessEvaluator searchReadinessEvaluator,
            SearchService searchService,
            RecommendationService recommendationService,
            HomeService homeService,
            ConsultService consultService,
            ExplanationService explanationService
    ) {
        this.criteriaService = criteriaService;
        this.contextService = contextService;
        this.searchReadinessEvaluator = searchReadinessEvaluator;
        this.searchService = searchService;
        this.recommendationService = recommendationService;
        this.homeService = homeService;
        this.consultService = consultService;
        this.explanationService = explanationService;
    }

    public RecommendationResponseDto handle(RecommendationRequestDto request) {

        if (request == null || request.getUserInput() == null) {
            return RecommendationResponseDto.invalid("요청이 올바르지 않습니다.");
        }

        log.info("[Orchestrator] handle start");

        /* 1️⃣ Criteria 생성 (이번 턴 입력) */
        RecommendationCriteria incoming =
                criteriaService.createCriteria(request.getUserInput());

        /* 2️⃣ CommandType 처리 */
        CommandType command = incoming.getCommandType();

        if (command == CommandType.RESET) {
            log.info("[Orchestrator] RESET → Context 초기화");
            contextService.reset();
        }

        if (command == CommandType.RETRY_SEARCH) {
            log.info("[Orchestrator] RETRY_SEARCH 감지");
            return handleRetrySearch();
        }

        /* 3️⃣ Context 병합 */
        contextService.merge(incoming);
        ConversationContext context = contextService.getContext();

        /* 4️⃣ 검색 가능 여부 판단 */
        SearchReadiness readiness =
                searchReadinessEvaluator.evaluate(context, incoming);

        if (readiness == SearchReadiness.NEED_MORE_CONTEXT) {
            return homeService.handle(
                    DecisionResult.discovery(
                            Decision.requery(
                                    ExplanationPolicy.REQUERY_NEED_MORE_CONDITION
                            )
                    ),
                    incoming
            );
        }

        /* =========================
         * 🔥 SEARCH 확정
         * ========================= */

        // 🔥 핵심 수정 1:
        // 검색은 무조건 Context 기준 Criteria 사용
        RecommendationCriteria criteriaForSearch =
                context.toCriteria();

        /* 5️⃣ Search */
        List<Product> products =
                searchService.search(criteriaForSearch);

        /* 6️⃣ Evaluation (점수 계산만) */
        recommendationService.evaluate(criteriaForSearch, products);

        /* 7️⃣ 응답 생성 */
        String message =
                explanationService.generateByPolicy(
                        ExplanationPolicy.RECOMMEND_CONFIDENT
                );

        List<RecommendationResponseDto.Item> items =
                convertToItems(products);

        return RecommendationResponseDto.recommend(
                items,
                message
        );
    }

    /**
     * RETRY_SEARCH 전용 처리
     */
    private RecommendationResponseDto handleRetrySearch() {

        ConversationContext context = contextService.getContext();
        RecommendationCriteria criteria = context.toCriteria();

        log.info("[Orchestrator] RETRY_SEARCH retryCount={}",
                context.getRetryCount());

        int offset = context.getRetryCount() * 5;

        List<Product> products =
                searchService.searchWithOffset(criteria, offset);

        context.increaseRetryCount();

        // Evaluation only
        recommendationService.evaluate(criteria, products);

        String message =
                explanationService.generateByPolicy(
                        ExplanationPolicy.RECOMMEND_CONFIDENT
                );

        List<RecommendationResponseDto.Item> items =
                convertToItems(products);

        return RecommendationResponseDto.recommend(
                items,
                message
        );
    }

    private List<RecommendationResponseDto.Item> convertToItems(
            List<Product> products
    ) {
        if (products == null || products.isEmpty()) {
            return List.of();
        }

        return products.stream()
                .limit(5)
                .map(product -> new RecommendationResponseDto.Item(
                        product.getId(),
                        product.getTitle(),
                        product.getImageUrl(),
                        product.getLink(),
                        product.getPrice(),
                        product.getBrand() != null
                                ? product.getBrand()
                                : "기타",
                        ""
                ))
                .collect(Collectors.toList());
    }
}
