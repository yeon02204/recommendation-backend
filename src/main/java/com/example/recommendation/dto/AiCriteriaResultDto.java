/**
 * OpenAI가 반환한 "조건 구조화 결과"를 그대로 담는 DTO
 * 판단 ❌ / 검증 ❌
 */

package com.example.recommendation.dto;

import java.util.List;

import com.example.recommendation.domain.criteria.CommandType;
import com.example.recommendation.domain.criteria.UserIntentType;

/**
 * OpenAI가 반환한 "조건 구조화 결과"를 그대로 담는 DTO
 * 판단 ❌ / 검증 ❌
 */
public class AiCriteriaResultDto {

    private String searchKeyword;
    private List<String> optionKeywords;
    private Integer priceMax;
    private String preferredBrand;

    // 🔥 의미 확장 필드
    private UserIntentType intentType;
    private CommandType commandType;

    public AiCriteriaResultDto() {}

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

    public UserIntentType getIntentType() {
        return intentType;
    }

    public CommandType getCommandType() {
        return commandType;
    }

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

    public void setIntentType(UserIntentType intentType) {
        this.intentType = intentType;
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType;
    }
}
