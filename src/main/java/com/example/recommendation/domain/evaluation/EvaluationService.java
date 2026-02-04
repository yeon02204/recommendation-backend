package com.example.recommendation.domain.evaluation;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.Product;

/**
 * [역할]
 * - 검색된 상품 목록을 평가하여
 *   추천 판단에 필요한 "사실 데이터"를 생성한다.
 *
 * [중요]
 * - 판단을 하지 않는다.
 * - RECOMMEND / REQUERY / INVALID 결정 ❌
 * - 오직 EvaluationResult에 들어갈 데이터만 계산
 *
 * [책임]
 * - 점수 계산
 * - 정렬
 * - 상위 N개 선별
 * - 집합 특성 계산 (hasBrandMatch / hasKeywordMatch)
 *
 * [MVP 기준]
 * - optionKeywords는 MVP 필수 신호
 * - 키워드 1개라도 매칭되면 +1점 (중복 매칭 금지)
 * - 브랜드 선호가 있고, 상품에 브랜드가 있으면 +1점
 */

/**
 * ⚠️ POLICY LOCK (MVP)
 *
 * 이 클래스의 점수 정책(+1 / +1)은 MVP 기준으로 "고정"되어 있다.
 *
 * - 점수 가중치 변경
 * - 조건 추가 (price, brand 동일성 등)
 * - 점수 해상도 확장
 *
 * ❌ 금지 (고도화 단계 전까지)
 *
 * 이 클래스는 "추천 품질"이 아니라
 * "Decision 흐름 안정성"을 검증하기 위한 최소 정책 구현체다.
 *
 * 정책 변경은 반드시:
 * 1) 정책 문서 수정
 * 2) 정책 테스트 수정
 * 3) 팀장 승인
 * 이후에만 가능하다.
 */

/**
 * [POLICY GUARD]
 *
 * ExplanationPolicy → 사용자 노출 문장은
 * 반드시 이 Service를 통해서만 생성한다.
 *
 * ❌ 외부 클래스에서 policy.getMessage() 직접 호출 금지
 * ❌ if / switch 로 문장 생성 금지
 *
 * 정책 ↔ 문장 매핑 단일 책임 보장용
 */

@Service
public class EvaluationService {
	
    private static final Logger log =
            LoggerFactory.getLogger(EvaluationService.class);
    
    public EvaluationResult evaluate(
            List<Product> products,
            RecommendationCriteria criteria
    ) {

        // 0️⃣ 검색 결과 없음
        if (products == null || products.isEmpty()) {
            return EvaluationResult.empty();
        }

        // 1️⃣ 상품별 사실 생성 + 점수 계산 + 정렬 + 상위 5개
        List<EvaluatedProduct> evaluatedProducts =
                products.stream()
                        .map(product -> {

                            /* =========================
                             * 1. brandMatch (사실)
                             * =========================
                             * - 브랜드 선호가 있고
                             * - 상품에 브랜드 정보가 존재하는 경우만 true
                             * - (기존 테스트 계약 유지)
                             */
                            boolean brandMatched =
                                    criteria.isBrandPreferred()
                                    && product.hasBrand();

                            /* =========================
                             * 2. optionKeywordMatch (사실)
                             * =========================
                             * - 키워드 1개라도 매칭되면 true
                             * - 중복 매칭 절대 금지
                             */
                            boolean optionKeywordMatched = false;
                            if (criteria.getOptionKeywords() != null) {
                                for (String kw : criteria.getOptionKeywords()) {
                                    if (product.getTitle().contains(kw)) {
                                        optionKeywordMatched = true;
                                        break; // ⭐ 중복 매칭 방지
                                    }
                                }
                            }

                            /* =========================
                             * 3. 점수 계산 (MVP 정책)
                             * =========================
                             * - optionKeywordMatch: +1
                             * - brandMatch: +1
                             */
                            int score = 0;
                            if (optionKeywordMatched) score += 1;
                            if (brandMatched) score += 1;

                            return new EvaluatedProduct(
                                    product,
                                    score,
                                    brandMatched,
                                    optionKeywordMatched
                            );
                        })
                        .sorted(
                                Comparator.comparingInt(
                                        EvaluatedProduct::getScore
                                ).reversed()
                        )
                        .limit(5)
                        .toList();
        log.info("===== Evaluation Observation Start =====");

        for (int i = 0; i < evaluatedProducts.size(); i++) {
            EvaluatedProduct p = evaluatedProducts.get(i);

            log.info(
                "[EVAL:{}] title='{}', score={}, brandMatch={}, keywordMatch={}",
                i,
                p.getProduct().getTitle(),
                p.getScore(),
                p.hasBrandMatch(),
                p.hasKeywordMatch()
            );
        }

        log.info("===== Evaluation Observation End =====");
        

        /* =========================
         * 4. 집합 특성 계산 (사실)
         * ========================= */

        boolean hasBrandMatch =
                evaluatedProducts.stream()
                        .anyMatch(EvaluatedProduct::hasBrandMatch);

        boolean hasKeywordMatch =
                evaluatedProducts.stream()
                        .anyMatch(EvaluatedProduct::hasKeywordMatch);

        // 5️⃣ EvaluationResult 생성
        return EvaluationResult.of(
                evaluatedProducts,
                hasKeywordMatch,
                hasBrandMatch
        );
    }
}
