package com.example.recommendation.repository;

import com.example.recommendation.entity.RecommendationRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRequestRepository
        extends JpaRepository<RecommendationRequest, Long> {
}
