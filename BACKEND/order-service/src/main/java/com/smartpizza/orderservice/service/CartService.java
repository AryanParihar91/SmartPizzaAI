package com.smartpizza.orderservice.service;

import com.smartpizza.orderservice.dto.AddCartItemRequest;
import com.smartpizza.orderservice.dto.CartItemResponse;

import java.util.List;

public interface CartService {

    CartItemResponse addToCart(Long userId, AddCartItemRequest request);

    List<CartItemResponse> getCartByUser(Long userId);

    CartItemResponse updateCartItem(Long cartItemId, Integer quantity);

    void removeCartItem(Long cartItemId);

    void clearCart(Long userId);
}