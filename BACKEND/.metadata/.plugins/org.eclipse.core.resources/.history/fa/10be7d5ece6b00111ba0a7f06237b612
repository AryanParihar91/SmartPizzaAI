package com.smartpizza.recommendationservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartpizza.recommendationservice.client.MenuClient;
import com.smartpizza.recommendationservice.client.OrderClient;
import com.smartpizza.recommendationservice.dto.PizzaResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

	private final OrderClient orderClient;
	private final MenuClient menuClient;

	@Override
	public List<PizzaResponse> getRecommendedPizzasForUser(Long userId) {

		List<Long> topPizzaIds = orderClient.getTopOrderedPizzaIdsByUser(userId);

		if (topPizzaIds == null || topPizzaIds.isEmpty()) {
			List<PizzaResponse> allPizzas = menuClient.getAllPizzas();

			if (allPizzas == null || allPizzas.isEmpty()) {
				return List.of();
			}

			return allPizzas.stream().filter(pizza -> Boolean.TRUE.equals(pizza.getAvailable())).limit(2).toList();
		}

		List<PizzaResponse> recommendedPizzas = new ArrayList<>();

		for (Long pizzaId : topPizzaIds) {
			try {
				PizzaResponse pizza = menuClient.getPizzaById(pizzaId);

				if (pizza != null && Boolean.TRUE.equals(pizza.getAvailable())) {
					recommendedPizzas.add(pizza);
				}
			} catch (Exception ignored) {
				// If one pizza is deleted/unavailable in menu-service, skip it
			}
		}

		return recommendedPizzas;
	}
}