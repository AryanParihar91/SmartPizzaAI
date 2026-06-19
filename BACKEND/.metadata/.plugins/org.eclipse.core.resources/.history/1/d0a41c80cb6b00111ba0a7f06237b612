package com.smartpizza.menuservice.service;

import com.smartpizza.menuservice.dto.PizzaRequest;
import com.smartpizza.menuservice.dto.PizzaResponse;
import com.smartpizza.menuservice.entity.Category;
import com.smartpizza.menuservice.entity.Pizza;
import com.smartpizza.menuservice.exception.ResourceNotFoundException;
import com.smartpizza.menuservice.repository.CategoryRepository;
import com.smartpizza.menuservice.repository.PizzaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PizzaServiceImpl implements PizzaService {

    private final PizzaRepository pizzaRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public PizzaResponse createPizza(PizzaRequest request) {

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Pizza pizza = new Pizza();
        pizza.setPizzaName(request.getPizzaName());
        pizza.setDescription(request.getDescription());
        pizza.setPrice(request.getPrice());
        pizza.setSize(request.getSize());
        pizza.setImageUrl(request.getImageUrl());
        pizza.setAvailable(request.getAvailable());
        pizza.setVeg(request.getVeg());
        pizza.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        pizza.setCategory(category);

        Pizza savedPizza = pizzaRepository.save(pizza);

        return convertToResponse(savedPizza);
    }

    @Override
    public List<PizzaResponse> getAllPizzas() {

        List<Pizza> pizzas = pizzaRepository.findAll();
        List<PizzaResponse> responses = new ArrayList<>();

        for (Pizza pizza : pizzas) {
            responses.add(convertToResponse(pizza));
        }

        return responses;
    }

    @Override
    public List<PizzaResponse> getAvailablePizzas() {

        List<Pizza> pizzas = pizzaRepository.findByAvailableTrue();
        List<PizzaResponse> responses = new ArrayList<>();

        for (Pizza pizza : pizzas) {
            responses.add(convertToResponse(pizza));
        }

        return responses;
    }

    @Override
    public PizzaResponse getPizzaById(Long pizzaId) {

        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza not found with id: " + pizzaId));

        return convertToResponse(pizza);
    }

    @Override
    public List<PizzaResponse> getPizzasByCategory(Long categoryId) {

        List<Pizza> pizzas = pizzaRepository.findByCategoryCategoryId(categoryId);
        List<PizzaResponse> responses = new ArrayList<>();

        for (Pizza pizza : pizzas) {
            responses.add(convertToResponse(pizza));
        }

        return responses;
    }

    @Override
    public List<PizzaResponse> getPizzasByVegType(Boolean veg) {

        List<Pizza> pizzas = pizzaRepository.findByVeg(veg);
        List<PizzaResponse> responses = new ArrayList<>();

        for (Pizza pizza : pizzas) {
            responses.add(convertToResponse(pizza));
        }

        return responses;
    }

    @Override
    public PizzaResponse updatePizza(Long pizzaId, PizzaRequest request) {

        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza not found with id: " + pizzaId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        pizza.setPizzaName(request.getPizzaName());
        pizza.setDescription(request.getDescription());
        pizza.setPrice(request.getPrice());
        pizza.setSize(request.getSize());
        pizza.setImageUrl(request.getImageUrl());
        pizza.setAvailable(request.getAvailable());
        pizza.setVeg(request.getVeg());
        pizza.setPreparationTimeMinutes(request.getPreparationTimeMinutes());
        pizza.setCategory(category);

        Pizza updatedPizza = pizzaRepository.save(pizza);

        return convertToResponse(updatedPizza);
    }

    @Override
    public void deletePizza(Long pizzaId) {

        Pizza pizza = pizzaRepository.findById(pizzaId)
                .orElseThrow(() -> new ResourceNotFoundException("Pizza not found with id: " + pizzaId));

        pizzaRepository.delete(pizza);
    }

    private PizzaResponse convertToResponse(Pizza pizza) {

        PizzaResponse response = new PizzaResponse();
        response.setPizzaId(pizza.getPizzaId());
        response.setPizzaName(pizza.getPizzaName());
        response.setDescription(pizza.getDescription());
        response.setPrice(pizza.getPrice());
        response.setSize(pizza.getSize());
        response.setImageUrl(pizza.getImageUrl());
        response.setAvailable(pizza.getAvailable());
        response.setVeg(pizza.getVeg());
        response.setPreparationTimeMinutes(pizza.getPreparationTimeMinutes());

        if (pizza.getCategory() != null) {
            response.setCategoryId(pizza.getCategory().getCategoryId());
            response.setCategoryName(pizza.getCategory().getCategoryName());
        }

        return response;
    }
}
