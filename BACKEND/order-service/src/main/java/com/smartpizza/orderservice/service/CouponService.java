package com.smartpizza.orderservice.service;

import com.smartpizza.orderservice.dto.CouponApplyRequest;
import com.smartpizza.orderservice.dto.CouponDetailsResponse;
import com.smartpizza.orderservice.dto.CouponRequest;
import com.smartpizza.orderservice.dto.CouponResponse;

import java.util.List;

public interface CouponService {

    CouponDetailsResponse createCoupon(CouponRequest request);

    List<CouponDetailsResponse> getAllCoupons();

    CouponResponse applyCoupon(CouponApplyRequest request);
}