package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponDetailsResponse {

    private Long couponId;
    private String couponCode;
    private String description;
    private Double discountPercentage;
    private Double minimumOrderAmount;
    private Boolean active;
}