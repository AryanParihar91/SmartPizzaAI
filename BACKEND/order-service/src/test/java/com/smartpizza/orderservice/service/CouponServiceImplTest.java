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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    private Coupon coupon;
    private CouponRequest couponRequest;
    private CartItem cartItem;

    @BeforeEach
    void setup() {

        coupon = new Coupon();
        coupon.setCouponId(1L);
        coupon.setCouponCode("SAVE10");
        coupon.setDescription("10 percent discount");
        coupon.setDiscountPercentage(10.0);
        coupon.setMinimumOrderAmount(300.0);
        coupon.setActive(true);

        couponRequest = new CouponRequest();
        couponRequest.setCouponCode("SAVE10");
        couponRequest.setDescription("10 percent discount");
        couponRequest.setDiscountPercentage(10.0);
        couponRequest.setMinimumOrderAmount(300.0);
        couponRequest.setActive(true);

        cartItem = new CartItem();
        cartItem.setCartItemId(1L);
        cartItem.setUserId(1L);
        cartItem.setPizzaId(1L);
        cartItem.setPizzaName("Farmhouse Pizza");
        cartItem.setPrice(349.0);
        cartItem.setQuantity(2);
        cartItem.setTotalPrice(698.0);
    }

    @Test
    void createCouponShouldReturnCouponDetailsResponse() {

        when(couponRepository.save(any(Coupon.class))).thenReturn(coupon);

        CouponDetailsResponse response = couponService.createCoupon(couponRequest);

        assertNotNull(response);
        assertEquals(1L, response.getCouponId());
        assertEquals("SAVE10", response.getCouponCode());
        assertEquals(10.0, response.getDiscountPercentage());
        assertEquals(300.0, response.getMinimumOrderAmount());
        assertTrue(response.getActive());

        verify(couponRepository, times(1)).save(any(Coupon.class));
    }

    @Test
    void getAllCouponsShouldReturnCouponList() {

        when(couponRepository.findAll()).thenReturn(List.of(coupon));

        List<CouponDetailsResponse> responses = couponService.getAllCoupons();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("SAVE10", responses.get(0).getCouponCode());

        verify(couponRepository, times(1)).findAll();
    }

    @Test
    void applyCouponWhenValidCouponShouldApplyDiscount() {

        CouponApplyRequest request = new CouponApplyRequest();
        request.setUserId(1L);
        request.setCouponCode("SAVE10");

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(couponRepository.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

        CouponResponse response = couponService.applyCoupon(request);

        assertNotNull(response);
        assertEquals("SAVE10", response.getCouponCode());
        assertEquals(698.0, response.getSubtotal());
        assertEquals(69.8, response.getDiscountAmount());
        assertEquals(628.2, response.getFinalAmount());
        assertEquals("Coupon applied successfully", response.getMessage());

        verify(cartItemRepository, times(1)).findByUserId(1L);
        verify(couponRepository, times(1)).findByCouponCode("SAVE10");
    }

    @Test
    void applyCouponWhenCartIsEmptyShouldThrowException() {

        CouponApplyRequest request = new CouponApplyRequest();
        request.setUserId(1L);
        request.setCouponCode("SAVE10");

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        assertThrows(RuntimeException.class, () -> {
            couponService.applyCoupon(request);
        });

        verify(couponRepository, never()).findByCouponCode(anyString());
    }

    @Test
    void applyCouponWhenCouponNotFoundShouldThrowException() {

        CouponApplyRequest request = new CouponApplyRequest();
        request.setUserId(1L);
        request.setCouponCode("WRONG");

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(couponRepository.findByCouponCode("WRONG")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            couponService.applyCoupon(request);
        });

        verify(couponRepository, times(1)).findByCouponCode("WRONG");
    }

    @Test
    void applyCouponWhenCouponInactiveShouldThrowException() {

        CouponApplyRequest request = new CouponApplyRequest();
        request.setUserId(1L);
        request.setCouponCode("SAVE10");

        coupon.setActive(false);

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(couponRepository.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

        assertThrows(RuntimeException.class, () -> {
            couponService.applyCoupon(request);
        });
    }

    @Test
    void applyCouponWhenMinimumAmountNotMetShouldThrowException() {

        CouponApplyRequest request = new CouponApplyRequest();
        request.setUserId(1L);
        request.setCouponCode("SAVE10");

        coupon.setMinimumOrderAmount(1000.0);

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(couponRepository.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

        assertThrows(RuntimeException.class, () -> {
            couponService.applyCoupon(request);
        });
    }
}