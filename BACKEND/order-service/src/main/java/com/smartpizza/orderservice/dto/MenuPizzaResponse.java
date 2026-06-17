package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuPizzaResponse {

    private Long pizzaId;
    private String pizzaName;
    private String description;
    private Double price;
    private String size;
    private Boolean available;
    private Boolean veg;
    private Integer preparationTimeMinutes;
    private Long categoryId;
    private String categoryName;
}