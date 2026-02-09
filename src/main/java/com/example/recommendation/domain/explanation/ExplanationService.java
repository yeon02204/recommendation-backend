package com.example.recommendation.domain.explanation;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.ConversationPhase;
import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.decision.DecisionResult;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;
import com.example.recommendation.external.openai.OpenAiExplanationClient;

/**
 * ExplanationService
 *
 * [역할]
 * - DecisionResult + Criteria를
 *   사용자에게 이해 가능한 문장으로 변환
 *
 * [책임 범위]
 * - READY 단계 요약 / 방향 문장 ⭕
 * - 정책 기반 설명 문장 ⭕
 *
 * [절대 금지]
 * - 판단 ❌
 * - 검색 ❌
 * - 상태 전이 ❌
 */
@Service
public class ExplanationService {

    private static final Logger log =
            LoggerFactory.getLogger(ExplanationService.class);

    private final OpenAiExplanationClient openAiClient;

    public ExplanationService(OpenAiExplanationClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    /* =========================
     * SEARCH 이후 설명 (AI)
     * ========================= */

    public String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    ) {
        log.info("[ExplanationService] generateExplanation (AI)");
        return openAiClient.generateExplanation(products, criteria);
    }

    public Map<Long, String> generateCardExplanations(
            List<CardExplanationPrompt> prompts,
            RecommendationCriteria criteria
    ) {
        log.info("[ExplanationService] generateCardExplanations (AI)");
        return openAiClient.generateCardExplanations(prompts, criteria);
    }

    /* =========================
     * READY / HOME 설명 (정책)
     * ========================= */

    /**
     * READY 단계 전용 요약 / 방향 문장 생성
     *
     * - AI 호출 ❌
     * - DecisionResult 의미만 해석
     */
    public String generateReadySummary(
            DecisionResult decisionResult,
            RecommendationCriteria criteria
    ) {
        ConversationPhase phase = decisionResult.getNextPhase();
        String reasoning = decisionResult.getReasoning();

        log.info(
            "[ExplanationService] generateReadySummary phase={}, reasoning={}",
            phase,
            reasoning
        );

        // 안전망: READY 아닌데 호출된 경우
        if (phase != ConversationPhase.READY) {
            log.warn(
                "[ExplanationService] generateReadySummary called in non-READY phase={}",
                phase
            );
            return ExplanationPolicy
                    .REQUERY_NEED_MORE_CONDITION
                    .getMessage();
        }

        /*
         * 🔹 현재 단계의 책임
         * - reasoning을 "정책 키"로 해석
         * - 문장은 policy가 가진다
         * - 아직 분기 단순화 OK
         */
        switch (reasoning) {

            case "INSUFFICIENT_CONTEXT":
                return ExplanationPolicy
                        .REQUERY_MAINKEYWORD_MISSING
                        .getMessage();

            case "ENOUGH_CONTEXT_BUT_SEARCH_NOT_STARTED":
                return ExplanationPolicy
                        .REQUERY_NEED_MORE_CONDITION
                        .getMessage();

            default:
                // 알 수 없는 reasoning → 기본 안전 문장
                log.warn(
                    "[ExplanationService] unknown reasoning={}, fallback default",
                    reasoning
                );
                return ExplanationPolicy
                        .REQUERY_NEED_MORE_CONDITION
                        .getMessage();
        }
    }

    /* =========================
     * Policy 직접 변환
     * ========================= */

    public String generateByPolicy(ExplanationPolicy policy) {
        log.info(
            "[ExplanationService] generateByPolicy policy={}",
            policy
        );
        return policy.getMessage();
    }
}
