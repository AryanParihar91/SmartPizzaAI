package com.smartpizza.menuservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PizzaResponse {

    private Long pizzaId;
    private String pizzaName;
    private String description;
    private Double price;
    private String size;
    private String imageUrl;
    private Boolean available;
    private Boolean veg;
    private Integer preparationTimeMinutes;

    private Long categoryId;
    private String categoryName;
}