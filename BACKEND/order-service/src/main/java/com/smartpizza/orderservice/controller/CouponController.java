package com.smartpizza.orderservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartpizza.orderservice.dto.CouponApplyRequest;
import com.smartpizza.orderservice.dto.CouponDetailsResponse;
import com.smartpizza.orderservice.dto.CouponRequest;
import com.smartpizza.orderservice.dto.CouponResponse;
import com.smartpizza.orderservice.service.CouponService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    //create coupon
    @PostMapping("/create")
    public CouponDetailsResponse createCoupon(@Valid @RequestBody CouponRequest request) {
        return couponService.createCoupon(request);
    }

    //get all coupons
    @GetMapping
    public List<CouponDetailsResponse> getAllCoupons() {
        return couponService.getAllCoupons();
    }

    //apply coupon api
    @PostMapping("/apply")
    public CouponResponse applyCoupon(@Valid @RequestBody CouponApplyRequest request) {
        return couponService.applyCoupon(request);
    }
}