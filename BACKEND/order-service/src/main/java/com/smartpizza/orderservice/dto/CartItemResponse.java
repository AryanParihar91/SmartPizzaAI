package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemResponse {

    private Long cartItemId;
    private Long userId;
    private Long pizzaId;
    private String pizzaName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}