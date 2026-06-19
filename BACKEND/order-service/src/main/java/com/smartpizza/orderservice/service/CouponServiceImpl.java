package com.smartpizza.orderservice.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

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
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private final CouponRepository couponRepository;
	private final CartItemRepository cartItemRepository;

	@Override
	public CouponDetailsResponse createCoupon(CouponRequest request) {

		log.info("Creating coupon with code: {}", request.getCouponCode());

		Coupon coupon = new Coupon();

		coupon.setCouponCode(request.getCouponCode());
		coupon.setDescription(request.getDescription());
		coupon.setDiscountPercentage(request.getDiscountPercentage());
		coupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
		coupon.setActive(request.getActive());

		Coupon savedCoupon = couponRepository.save(coupon);

		log.info("Coupon created successfully with ID: {}", savedCoupon.getCouponId());

		return convertToDetailsResponse(savedCoupon);
	}

	@Override
	public List<CouponDetailsResponse> getAllCoupons() {

		log.info("Fetching all coupons");

		List<Coupon> coupons = couponRepository.findAll();

		List<CouponDetailsResponse> responses = new ArrayList<>();

		for (Coupon coupon : coupons) {

			responses.add(convertToDetailsResponse(coupon));
		}

		log.info("Total coupons fetched: {}", responses.size());

		return responses;
	}

	@Override
	public CouponResponse applyCoupon(CouponApplyRequest request) {

		log.info("Applying coupon {} for user ID: {}", request.getCouponCode(), request.getUserId());

		List<CartItem> cartItems = cartItemRepository.findByUserId(request.getUserId());

		if (cartItems.isEmpty()) {

			log.warn("Coupon application failed - Cart is empty for user ID: {}", request.getUserId());
			
			throw new RuntimeException("Cart is empty");
		}

		Coupon coupon = couponRepository.findByCouponCode(request.getCouponCode()).orElseThrow(() -> {

			log.error("Coupon not found: {}", request.getCouponCode());
			
			return new ResourceNotFoundException("Coupon not found: " + request.getCouponCode());
		});

		if (coupon.getActive() != null && !coupon.getActive()) {

			log.warn("Inactive coupon attempted: {}", coupon.getCouponCode());

			throw new RuntimeException("Coupon is not active");
		}

		double subtotal = 0;

		for (CartItem item : cartItems) {

			subtotal = subtotal + item.getTotalPrice();
		}

		log.info("Calculated subtotal for user ID {} : {}", request.getUserId(), subtotal);

		if (coupon.getMinimumOrderAmount() != null && subtotal < coupon.getMinimumOrderAmount()) {

			log.warn("Minimum order amount not met for coupon: {}", coupon.getCouponCode());

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

		log.info("Coupon {} applied successfully. Discount: {}", coupon.getCouponCode(), discountAmount);

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