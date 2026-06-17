package com.smartpizza.menuservice.service;

import com.smartpizza.menuservice.dto.PizzaRequest;
import com.smartpizza.menuservice.dto.PizzaResponse;

import java.util.List;

public interface PizzaService {

    PizzaResponse createPizza(PizzaRequest request);

    List<PizzaResponse> getAllPizzas();

    List<PizzaResponse> getAvailablePizzas();

    PizzaResponse getPizzaById(Long pizzaId);

    List<PizzaResponse> getPizzasByCategory(Long categoryId);

    List<PizzaResponse> getPizzasByVegType(Boolean veg);

    PizzaResponse updatePizza(Long pizzaId, PizzaRequest request);

    void deletePizza(Long pizzaId);
}