package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponResponse {

    private String couponCode;
    private Double subtotal;
    private Double discountAmount;
    private Double finalAmount;
    private String message;
}
