package com.smartpizza.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponRequest {

    @NotBlank(message = "Coupon code is required")
    private String couponCode;

    private String description;

    @NotNull(message = "Discount percentage is required")
    @Positive(message = "Discount percentage must be greater than 0")
    private Double discountPercentage;

    private Double minimumOrderAmount;

    private Boolean active;
}