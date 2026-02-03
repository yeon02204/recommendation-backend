/**
 * OpenAI가 반환한 "조건 구조화 결과"를 그대로 담는 DTO
 * 판단 ❌ / 검증 ❌
 */

package com.example.recommendation.dto;

import java.util.List;

/**
 * OpenAI가 반환한 "조건 구조화 결과"를 그대로 담는 DTO
 * 판단 ❌ / 검증 ❌
 */
public class AiCriteriaResultDto {

    private String searchKeyword;
    private List<String> optionKeywords;
    private Integer priceMax;
    private String preferredBrand;

    public AiCriteriaResultDto() {
        // JSON 역직렬화용 기본 생성자
    }

    // ===== Getter =====

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

    // ===== Setter (중요) =====

    public void setSearchKeyword(String searchKeyword) {
        this.searchKeyword = searchKeyword;
    }

    public void setOptionKeywords(List<String> optionKeywords) {
        this.optionKeywords = optionKeywords;
    }

    public void setPriceMax(Integer priceMax) {
        this.priceMax = priceMax;
    }

    public void setPreferredBrand(String preferredBrand) {
        this.preferredBrand = preferredBrand;
    }
}