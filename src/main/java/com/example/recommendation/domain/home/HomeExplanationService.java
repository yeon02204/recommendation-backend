package com.example.recommendation.domain.home;

import com.example.recommendation.domain.criteria.RecommendationCriteria;

/**
 * HOME 단계 전용 설명 생성기
 *
 * [역할]
 * - HOME 상태에서 사용자에게 보여줄 "다음 대화 문장" 생성
 *
 * [원칙]
 * - 판단 ❌
 * - 검색 ❌
 * - 상태 변경 ❌
 * - 오직 문장 생성 ⭕
 * 
 * HOME 단계에서 어떤 AI를 호출할지 조합하는 상위 설명 서비스
 */
public interface HomeExplanationService {

    /**
     * HOME 사유 기반 재질문 문장 생성
     */
    String generateRequery(HomeReason reason, RecommendationCriteria criteria);

    /**
     * 검색 직전 READY 단계 요약 문장 생성
     */
    String generateReadySummary(RecommendationCriteria criteria);
}
