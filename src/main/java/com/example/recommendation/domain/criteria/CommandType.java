package com.example.recommendation.domain.criteria;

/**
 * 사용자 입력의 명령 의도를 나타냄
 *
 * - APPEND       : 기존 조건에 추가 (기본값)
 * - RESET        : 조건 완전 초기화 후 새로 시작
 * - RETRY_SEARCH : 같은 조건으로 다른 상품 보기
 *
 * ⚠️ 판단은 AI(OpenAI)가 수행
 * ⚠️ 실행은 Orchestrator가 수행
 */
public enum CommandType {
    APPEND,         // 조건 유지 + 추가
    RESET,          // 조건 초기화
    RETRY_SEARCH    // 같은 조건, 다른 결과
}