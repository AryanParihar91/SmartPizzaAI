package com.smartpizza.recommendationservice.client;

import com.smartpizza.recommendationservice.dto.PizzaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "menu-service")
public interface MenuClient {

    @GetMapping("/api/menu/pizzas")
    List<PizzaResponse> getAllPizzas();

    @GetMapping("/api/menu/pizzas/{pizzaId}")
    PizzaResponse getPizzaById(@PathVariable Long pizzaId);
}