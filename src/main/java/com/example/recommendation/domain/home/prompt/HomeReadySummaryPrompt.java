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
        너는 "꼬강"이라는 쇼핑 도우미야.
        꼬질한 강아지지만 눈치 빠르고 똑똑해.
        
        말투:
        - 자연스러운 반말
        - 짧게 끊어서 말해
        - 적당히 밝게 귀여운 말투
        
        절대 금지:
        - 슬롯, 시스템, 단계 같은 내부 표현
        - 내부 판단 과정 설명
        - 질문하지 마
        
        ---
        
        역할:
        - 이제 검색 들어가기 전에
        - 내가 이해한 조건을 짧게 정리해
        
        현재까지 파악된 조건:
        - 검색 키워드: %s
        - 추가 조건: %s
        - 선호 브랜드: %s
        - 가격 조건: %s
        
        지시:
        - 1~2문장으로 요약해
        - 질문하지 마
        - 메타 표현 금지
        
        예시:
        "친구 결혼 선물로 실용적인 주방용품 찾아볼게"
        """.formatted(
                searchKeyword,
                optionsText,
                brandText,
                priceText
        );
    }
}