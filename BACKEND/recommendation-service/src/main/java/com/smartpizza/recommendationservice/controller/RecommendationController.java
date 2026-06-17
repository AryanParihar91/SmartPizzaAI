package com.smartpizza.recommendationservice.controller;

import com.smartpizza.recommendationservice.dto.PizzaResponse;
import com.smartpizza.recommendationservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/top-pizzas")
    public List<PizzaResponse> getRecommendedPizzasForUser(@RequestHeader("X-User-Id") Long userId) {
        return recommendationService.getRecommendedPizzasForUser(userId);
    }
}