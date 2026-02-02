//package com.example.recommendation.external.openai;
//
//import java.util.List;
//
//import com.example.recommendation.domain.criteria.RecommendationCriteria;
//import com.example.recommendation.domain.evaluation.EvaluatedProduct;
//
//public interface OpenAiExplanationClient {
//
//    String generateExplanation(
//            List<EvaluatedProduct> products,
//            RecommendationCriteria criteria
//    );
//}

package com.example.recommendation.external.openai;

import java.util.List;

import com.example.recommendation.domain.criteria.RecommendationCriteria;
import com.example.recommendation.domain.evaluation.EvaluatedProduct;

public interface OpenAiExplanationClient {

    String generateExplanation(
            List<EvaluatedProduct> products,
            RecommendationCriteria criteria
    );
}