package com.example.recommendation.domain.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.home.ai.ReadySummaryAI;
import com.example.recommendation.domain.home.prompt.HomeReadySummaryPrompt;

/**
 * HOME 설명 생성 구현체
 *
 * ⚠️ 추천 완료 설명과 절대 섞이지 않는다
 * 
 * DISCOVERY / GUIDE / READY 중 어떤 설명 로직을 쓸지 실제로 위임하는 구현체
 */
@Service
public class DefaultHomeExplanationService
        implements HomeExplanationService {

    private static final Logger log =
            LoggerFactory.getLogger(DefaultHomeExplanationService.class);
    
    private final ReadySummaryAI readySummaryAI;
    
    public DefaultHomeExplanationService(ReadySummaryAI readySummaryAI) {
        this.readySummaryAI = readySummaryAI;
    }

    @Override
    public String generateRequery(
            HomeReason reason,
            RecommendationCriteria criteria
    ) {

        log.info("[HomeExplanation] requery reason={}", reason);

        // 🔥 MVP 단계: 고정 문장
        return switch (reason) {
            case NO_KEYWORD ->
                    "어떤 상품을 찾고 계신가요? 예: 헤드셋, 노트북";
            case NEED_MORE_CONDITION ->
                    "조금 더 구체적인 조건이 있으면 추천이 쉬워져요.";
            case AFTER_RESET ->
                    "처음부터 다시 추천을 도와드릴게요.";
            case AFTER_RETRY ->
                    "다른 조건으로 다시 살펴볼까요?";
            default ->
                    "조금만 더 알려주시면 도와드릴게요.";
        };
    }

    @Override
    public String generateReadySummary(
            RecommendationCriteria criteria
    ) {

        log.info("[HomeExplanation] generate READY_SUMMARY");

        // 🔥 STEP 12: 프롬프트 생성 → AI 위임
        HomeReadySummaryPrompt prompt =
                new HomeReadySummaryPrompt(criteria);
        
        return readySummaryAI.generate(prompt);
    }
}