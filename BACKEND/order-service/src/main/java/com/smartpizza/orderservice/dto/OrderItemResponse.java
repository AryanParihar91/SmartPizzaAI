package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {

    private Long orderItemId;
    private Long pizzaId;
    private String pizzaName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}