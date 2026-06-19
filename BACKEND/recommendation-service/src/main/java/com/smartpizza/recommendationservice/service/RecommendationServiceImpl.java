package com.smartpizza.recommendationservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.smartpizza.recommendationservice.client.MenuClient;
import com.smartpizza.recommendationservice.client.OrderClient;
import com.smartpizza.recommendationservice.dto.PizzaResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceImpl implements RecommendationService {

	private final OrderClient orderClient;
	private final MenuClient menuClient;

	@Override
	public List<PizzaResponse> getRecommendedPizzasForUser(Long userId) {

		log.info("Fetching recommended pizzas for user ID: {}", userId);

		List<Long> topPizzaIds = orderClient.getTopOrderedPizzaIdsByUser(userId);

		if (topPizzaIds == null || topPizzaIds.isEmpty()) {

			log.warn("No top ordered pizzas found for user ID: {}", userId);

			List<PizzaResponse> allPizzas = menuClient.getAllPizzas();

			if (allPizzas == null || allPizzas.isEmpty()) {

				log.warn("No pizzas available in menu service");

				return List.of();
			}

			log.info("Returning default available pizzas");

			return allPizzas.stream().filter(pizza -> Boolean.TRUE.equals(pizza.getAvailable())).limit(2).toList();
		}

		List<PizzaResponse> recommendedPizzas = new ArrayList<>();

		for (Long pizzaId : topPizzaIds) {

			try {

				log.info("Fetching pizza details for pizza ID: {}", pizzaId);

				PizzaResponse pizza = menuClient.getPizzaById(pizzaId);

				if (pizza != null && Boolean.TRUE.equals(pizza.getAvailable())) {

					recommendedPizzas.add(pizza);

					log.info("Pizza added to recommendations: {}", pizza.getPizzaName());
				}

			} catch (Exception exception) {

				log.error("Error fetching pizza from menu service for pizza ID: {}", pizzaId);
			}
		}

		log.info("Total recommended pizzas for user ID {} : {}", userId, recommendedPizzas.size());

		return recommendedPizzas;
	}
}