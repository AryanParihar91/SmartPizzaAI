package com.smartpizza.recommendationservice.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "order-service")
public interface OrderClient {

	@GetMapping("/api/orders/recommendations/top-pizza-ids")
	List<Long> getTopOrderedPizzaIdsByUser(@RequestHeader("X-User-Id") Long userId);
}