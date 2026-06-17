package com.smartpizza.orderservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CouponApplyRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotBlank(message = "Coupon code is required")
    private String couponCode;
}
