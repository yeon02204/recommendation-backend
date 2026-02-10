package com.example.recommendation.domain.home.prompt;

import java.util.List;
import java.util.stream.Collectors;

import com.example.recommendation.domain.criteria.RecommendationCriteria;

/**
 * READY_SUMMARY 전용 프롬프트 모델
 *
 * [역할]
 * - HOME → SEARCH 직전에
 *   "지금까지 이렇게 이해했고,
 *    이제 이 조건으로 검색을 진행한다"
 *   는 요약 문장을 만들기 위한 입력 데이터
 *
 * [절대 금지]
 * - 질문 생성 ❌
 * - 판단 / 평가 ❌
 * - 상품 추천 ❌
 *
 * → 요약 + 안내 문장만 생성
 * 
 * READY_SUMMARY_AI에 전달할 요약용 입력 정보를 프롬프트 텍스트로 변환하는 모델
 */
public class HomeReadySummaryPrompt {

    private final String searchKeyword;
    private final List<String> optionKeywords;
    private final String preferredBrand;
    private final Integer priceMax;

    public HomeReadySummaryPrompt(RecommendationCriteria criteria) {
        this.searchKeyword = criteria.getSearchKeyword();
        this.optionKeywords = criteria.getOptionKeywords();
        this.preferredBrand = criteria.getPreferredBrand();
        this.priceMax = criteria.getPriceMax();
    }

    /**
     * AI에게 전달할 READY_SUMMARY 프롬프트
     */
    public String toPromptText() {

        String optionsText =
                (optionKeywords == null || optionKeywords.isEmpty())
                        ? "없음"
                        : optionKeywords.stream()
                                .collect(Collectors.joining(", "));

        String brandText =
                preferredBrand == null ? "없음" : preferredBrand;

        String priceText =
                priceMax == null ? "제한 없음" : priceMax + "원 이하";

        return """
        너는 쇼핑 추천 서비스의 상담자다.

        역할:
        - 지금까지 사용자가 말한 조건을 한 문장으로 자연스럽게 요약한다
        - 이제 이 조건으로 상품 검색을 진행할 것임을 안내한다

        절대 규칙:
        - 질문 금지
        - 상품명, 브랜드 추천 금지
        - 평가, 추측, 판단 금지
        - 설명은 짧고 명확하게

        현재까지 파악된 조건:
        - 검색 키워드: %s
        - 추가 조건: %s
        - 선호 브랜드: %s
        - 가격 조건: %s

        지시:
        - 위 조건을 자연스러운 한국어 문장 1~2문장으로 요약하라
        - 마지막에는 "이 조건으로 상품을 찾아보겠다"는 의미를 담아라
        """.formatted(
                searchKeyword,
                optionsText,
                brandText,
                priceText
        );
    }
}
