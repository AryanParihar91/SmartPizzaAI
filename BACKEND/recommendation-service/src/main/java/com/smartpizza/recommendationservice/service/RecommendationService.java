package com.smartpizza.recommendationservice.service;

import com.smartpizza.recommendationservice.dto.PizzaResponse;

import java.util.List;

public interface RecommendationService {

    List<PizzaResponse> getRecommendedPizzasForUser(Long userId);
}