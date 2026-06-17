package com.smartpizza.recommendationservice.repository;

import com.smartpizza.recommendationservice.entity.RecommendationLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationLogRepository extends JpaRepository<RecommendationLog, Long> {

    List<RecommendationLog> findByUserId(Long userId);
}
