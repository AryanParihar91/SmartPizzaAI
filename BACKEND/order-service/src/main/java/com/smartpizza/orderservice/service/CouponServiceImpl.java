package com.smartpizza.orderservice.service;

import com.smartpizza.orderservice.dto.CouponApplyRequest;
import com.smartpizza.orderservice.dto.CouponDetailsResponse;
import com.smartpizza.orderservice.dto.CouponRequest;
import com.smartpizza.orderservice.dto.CouponResponse;
import com.smartpizza.orderservice.entity.CartItem;
import com.smartpizza.orderservice.entity.Coupon;
import com.smartpizza.orderservice.exception.ResourceNotFoundException;
import com.smartpizza.orderservice.repository.CartItemRepository;
import com.smartpizza.orderservice.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public CouponDetailsResponse createCoupon(CouponRequest request) {

        Coupon coupon = new Coupon();
        coupon.setCouponCode(request.getCouponCode());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        coupon.setActive(request.getActive());

        Coupon savedCoupon = couponRepository.save(coupon);

        return convertToDetailsResponse(savedCoupon);
    }

    @Override
    public List<CouponDetailsResponse> getAllCoupons() {

        List<Coupon> coupons = couponRepository.findAll();
        List<CouponDetailsResponse> responses = new ArrayList<>();

        for (Coupon coupon : coupons) {
            responses.add(convertToDetailsResponse(coupon));
        }

        return responses;
    }

    @Override
    public CouponResponse applyCoupon(CouponApplyRequest request) {

        List<CartItem> cartItems = cartItemRepository.findByUserId(request.getUserId());

        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Coupon coupon = couponRepository.findByCouponCode(request.getCouponCode())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found: " + request.getCouponCode()));

        if (coupon.getActive() != null && !coupon.getActive()) {
            throw new RuntimeException("Coupon is not active");
        }

        double subtotal = 0;

        for (CartItem item : cartItems) {
            subtotal = subtotal + item.getTotalPrice();
        }

        if (coupon.getMinimumOrderAmount() != null && subtotal < coupon.getMinimumOrderAmount()) {
            throw new RuntimeException("Minimum order amount required: " + coupon.getMinimumOrderAmount());
        }

        double discountAmount = subtotal * coupon.getDiscountPercentage() / 100;
        double finalAmount = subtotal - discountAmount;

        CouponResponse response = new CouponResponse();
        response.setCouponCode(coupon.getCouponCode());
        response.setSubtotal(subtotal);
        response.setDiscountAmount(discountAmount);
        response.setFinalAmount(finalAmount);
        response.setMessage("Coupon applied successfully");

        return response;
    }

    private CouponDetailsResponse convertToDetailsResponse(Coupon coupon) {

        CouponDetailsResponse response = new CouponDetailsResponse();
        response.setCouponId(coupon.getCouponId());
        response.setCouponCode(coupon.getCouponCode());
        response.setDescription(coupon.getDescription());
        response.setDiscountPercentage(coupon.getDiscountPercentage());
        response.setMinimumOrderAmount(coupon.getMinimumOrderAmount());
        response.setActive(coupon.getActive());

        return response;
    }
}
