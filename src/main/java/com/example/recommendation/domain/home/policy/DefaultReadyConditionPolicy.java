package com.example.recommendation.domain.home.policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.recommendation.domain.home.state.HomeConversationState;

/**
 * 🔥 3-4턴 전략 READY 정책
 *
 * READY 조건 (단순화):
 * - searchKeyword 존재 (카테고리 확정)
 * - AND 축 1개 이상 확보:
 *   - 가격 정보
 *   - OR 옵션/선호도/컨텍스트
 *
 * 슬롯 개수 기준 완전 삭제
 */
@Component
public class DefaultReadyConditionPolicy implements ReadyConditionPolicy {

    private static final Logger log =
            LoggerFactory.getLogger(DefaultReadyConditionPolicy.class);

    @Override
    public boolean isReady(HomeConversationState state) {

        // 🔥 1단계: keyword 확보 여부
        boolean hasKeyword = state.hasConfirmedKeyword();

        // 🔥 2단계: 축 1개 이상 확보 여부
        boolean hasPrice = state.hasConfirmedPrice();
        boolean hasOption = state.hasConfirmedOption();
        boolean hasAxis = hasPrice || hasOption;

        boolean ready = hasKeyword && hasAxis;

        log.info(
            "[READY Policy] keyword={}, price={}, option={} → READY={}",
            hasKeyword,
            hasPrice,
            hasOption,
            ready
        );

        return ready;
    }
}