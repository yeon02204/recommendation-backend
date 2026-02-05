package com.example.recommendation.domain.evaluation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.external.naver.Product;

@Service
public class EvaluationService {

    private static final Logger log =
            LoggerFactory.getLogger(EvaluationService.class);

    public EvaluationResult evaluate(
            List<Product> products,
            RecommendationCriteria criteria
    ) {

        if (products == null || products.isEmpty()) {
            return EvaluationResult.empty();
        }

        List<EvaluatedProduct> evaluatedProducts =
                products.stream()
                        .map(product -> {

                            /* =========================
                             * 1. brandMatch (기존 사실)
                             * ========================= */
                            boolean brandMatched =
                                    criteria.isBrandPreferred()
                                    && product.hasBrand();

                            /* =========================
                             * 2. option match (확장 사실)
                             * ========================= */
                            OptionMatchResult optionMatch =
                                    collectOptionMatches(
                                            product,
                                            criteria.getOptionKeywords()
                                    );

                            boolean optionKeywordMatched =
                                    !optionMatch.getMatchedKeywords().isEmpty();

                            /* =========================
                             * 3. 점수 (MVP 고정)
                             * ========================= */
                            int score = 0;
                            if (optionKeywordMatched) score += 1;
                            if (brandMatched) score += 1;

                            return new EvaluatedProduct(
                                    product,
                                    score,
                                    brandMatched,
                                    optionKeywordMatched,
                                    optionMatch.getMatchedKeywords(),
                                    optionMatch.getMatchedFields()
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

        for (EvaluatedProduct p : evaluatedProducts) {
            log.info(
                "[OPTION_MATCH_SUMMARY] title='{}', keywords={}, fields={}",
                p.getProduct().getTitle(),
                p.getMatchedOptionKeywords(),
                p.getMatchedOptionFields()
            );
        }

        log.info("===== Evaluation Observation End =====");

        boolean hasBrandMatch =
                evaluatedProducts.stream()
                        .anyMatch(EvaluatedProduct::hasBrandMatch);

        boolean hasKeywordMatch =
                evaluatedProducts.stream()
                        .anyMatch(EvaluatedProduct::hasKeywordMatch);

        return EvaluationResult.of(
                evaluatedProducts,
                hasKeywordMatch,
                hasBrandMatch
        );
    }

    /* =========================
     * 옵션 매칭 수집기
     * ========================= */
    private OptionMatchResult collectOptionMatches(
            Product product,
            List<String> optionKeywords
    ) {
        Set<String> matchedKeywords = new LinkedHashSet<>();
        Set<MatchField> matchedFields = new LinkedHashSet<>();

        if (optionKeywords == null || optionKeywords.isEmpty()) {
            return new OptionMatchResult(
                    List.of(),
                    Set.of()
            );
        }

        String title = product.getTitle();
        String brand = product.getBrand();

        for (String keyword : optionKeywords) {
            if (keyword == null || keyword.isBlank()) {
                continue;
            }

            if (contains(title, keyword)) {
                matchedKeywords.add(keyword);
                matchedFields.add(MatchField.TITLE);
            }

            if (contains(brand, keyword)) {
                matchedKeywords.add(keyword);
                matchedFields.add(MatchField.BRAND);
            }
        }

        return new OptionMatchResult(
                new ArrayList<>(matchedKeywords),
                matchedFields
        );
    }

    private boolean contains(String target, String keyword) {
        return target != null && target.contains(keyword);
    }

    /* =========================
     * 내부 결과 캐리어 (record ❌)
     * ========================= */
    private static class OptionMatchResult {
        private final List<String> matchedKeywords;
        private final Set<MatchField> matchedFields;

        private OptionMatchResult(
                List<String> matchedKeywords,
                Set<MatchField> matchedFields
        ) {
            this.matchedKeywords = matchedKeywords;
            this.matchedFields = matchedFields;
        }

        public List<String> getMatchedKeywords() {
            return matchedKeywords;
        }

        public Set<MatchField> getMatchedFields() {
            return matchedFields;
        }
    }
}
