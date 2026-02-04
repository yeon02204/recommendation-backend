package com.example.recommendation.domain.criteria;

import java.util.List;

/**
 * [역할]
 * - 추천 판단에 필요한 조건을 담는 순수 도메인 객체
 *
 * [설계 원칙]
 * - 이 객체는 "상태(State)"만 가진다
 * - 판단 로직 ❌
 * - confidence 개념 ❌
 * - followUpQuestion ❌
 * - 외부 서비스 호출 ❌
 * - AI 호출 ❌
 *
 * [이 객체가 할 수 있는 것]
 * - CriteriaService가 해석한 결과를 담는다
 * - Search / Evaluation / Decision 단계에서 읽히기만 한다
 *
 * [중요]
 * - 이 객체는 절대 "추천 가능/불가능"을 말하지 않는다
 * - 모든 판단은 DecisionMaker의 책임이다
 */

/**
 * 추천 판단 이전 단계의 "조건 데이터"를 담는 순수 도메인 객체
 *
 * - 판단 로직 없음 - 상태 플래그 없음 - setter 없음 - null / 빈 값 허용
 */
public class RecommendationCriteria {

	private final String searchKeyword;
	private final List<String> optionKeywords;
	private final Integer priceMax;
	private final String preferredBrand;

	public RecommendationCriteria(String searchKeyword, List<String> optionKeywords, Integer priceMax,
			String preferredBrand) {
		this.searchKeyword = searchKeyword;

		// 🔑 핵심 수정:
		// optionKeywords는 null이 아닌 "빈 리스트"로 보존한다
		// EvaluationService는 이 값을 그대로 신뢰한다
		this.optionKeywords = optionKeywords == null ? List.of() : List.copyOf(optionKeywords);

		this.priceMax = priceMax;
		this.preferredBrand = preferredBrand;
	}

	public String getSearchKeyword() {
		return searchKeyword;
	}

	public List<String> getOptionKeywords() {
		return optionKeywords;
	}

	public Integer getPriceMax() {
		return priceMax;
	}

	public String getPreferredBrand() {
		return preferredBrand;
	}

	// ===== 🔽 EvaluationService 호환용 파생 메서드 (핵심) =====

	/**
	 * 브랜드 선호 여부 - 판단 아님 - preferredBrand 값 존재 여부만 노출
	 */
	public boolean isBrandPreferred() {
		return preferredBrand != null && !preferredBrand.isBlank();
	}

	/**
	 * 가격 조건 존재 여부 - EvaluationService의 기존 로직 호환용
	 */
	public String getPriceRange() {
		return priceMax != null ? "HAS_LIMIT" : null;
	}

	@Override
	public String toString() {
		return "RecommendationCriteria{" + "searchKeyword='" + searchKeyword + '\'' + ", optionKeywords="
				+ optionKeywords + ", priceMax=" + priceMax + ", preferredBrand='" + preferredBrand + '\'' + '}';
	}

}
