package com.smartpizza.menuservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PizzaRequest {

    @NotBlank(message = "Pizza name is required")
    private String pizzaName;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    private String size;

    private String imageUrl;

    private Boolean available;

    private Boolean veg;

    private Integer preparationTimeMinutes;

    @NotNull(message = "Category id is required")
    private Long categoryId;
}