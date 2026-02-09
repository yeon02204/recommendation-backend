package com.example.recommendation.domain.consult;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.ConversationContext;
import com.example.recommendation.dto.ConsultActionType;
import com.example.recommendation.dto.ConsultResponse;

/**
 * [역할]
 * - CONSULT(상담) 모드 전용 응답 생성
 *
 * [책임]
 * - 다음 행동 결정 (질문 vs 재검색)
 *
 * [중요]
 * - 추천 ❌
 * - 검색 실행 ❌
 * - 점수 ❌
 */
@Service
public class ConsultService {

    /**
     * Context 기반 상담 응답 생성
     * - 조건 충분 → 재검색
     * - 조건 부족 → 질문
     */
    public ConsultResponse createConsultResponse(
            ConversationContext context
    ) {

        // 🔥 1️⃣ 조건 충분 → 재검색
        if (isSearchReady(context)) {
            return new ConsultResponse(
                    ConsultActionType.RETRY_SEARCH,
                    "비슷한 조건으로 다른 결과도 찾아볼게요.",
                    List.of()
            );
        }

        // 🔥 2️⃣ 조건 부족 → 질문
        List<String> questions = new ArrayList<>();

        if (context.getConfirmedKeyword() == null) {
            questions.add("어떤 종류의 상품을 찾고 계신가요?");
        }

        if (context.getPriceMax() == null) {
            questions.add("예산대는 어느 정도가 편하신가요?");
        }

        if (context.getPreferredBrand() == null) {
            questions.add("선호하는 브랜드가 있을까요?");
        }

        if (questions.isEmpty()) {
            questions.add("조금 더 중요하게 보는 조건이 있을까요?");
        }

        return new ConsultResponse(
                ConsultActionType.ASK_MORE,
                "조건을 조금만 더 알려주면 더 정확하게 추천할 수 있어요.",
                questions
        );
    }

    /**
     * 🔹 재검색 가능 여부 판단
     * - 판단 ❌
     * - 상태 체크 ⭕
     */
    private boolean isSearchReady(ConversationContext context) {
        int count = 0;

        if (context.getConfirmedKeyword() != null) count++;
        if (context.getPriceMax() != null) count++;
        if (context.getPreferredBrand() != null) count++;
        if (!context.getOptionKeywords().isEmpty()) count++;

        return count >= 2;
    }
}