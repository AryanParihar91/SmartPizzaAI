package com.smartpizza.orderservice.service;

import com.smartpizza.orderservice.client.MenuClient;
import com.smartpizza.orderservice.dto.AddCartItemRequest;
import com.smartpizza.orderservice.dto.CartItemResponse;
import com.smartpizza.orderservice.dto.MenuPizzaResponse;
import com.smartpizza.orderservice.entity.CartItem;
import com.smartpizza.orderservice.exception.ResourceNotFoundException;
import com.smartpizza.orderservice.repository.CartItemRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemRepository cartItemRepository;
    private final MenuClient menuClient;

    @Override
    public CartItemResponse addToCart(Long userId, AddCartItemRequest request) {

        log.info("Add to cart request received. userId: {}, pizzaId: {}, quantity: {}",
                userId,
                request.getPizzaId(),
                request.getQuantity()
        );

        MenuPizzaResponse pizza = menuClient.getPizzaById(request.getPizzaId());

        log.info("Pizza details fetched from menu-service. pizzaId: {}, pizzaName: {}, available: {}",
                pizza.getPizzaId(),
                pizza.getPizzaName(),
                pizza.getAvailable()
        );

        if (pizza.getAvailable() != null && !pizza.getAvailable()) {
            log.warn("Add to cart failed. Pizza is unavailable. userId: {}, pizzaId: {}",
                    userId,
                    request.getPizzaId()
            );

            throw new RuntimeException("Pizza is currently not available");
        }

        CartItem cartItem;

        if (cartItemRepository.findByUserIdAndPizzaId(userId, request.getPizzaId()).isPresent()) {

            cartItem = cartItemRepository.findByUserIdAndPizzaId(userId, request.getPizzaId()).get();

            log.info("Pizza already exists in cart. Updating quantity. cartItemId: {}, oldQuantity: {}, addQuantity: {}",
                    cartItem.getCartItemId(),
                    cartItem.getQuantity(),
                    request.getQuantity()
            );

            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());

        } else {

            log.info("Pizza not found in cart. Creating new cart item. userId: {}, pizzaId: {}",
                    userId,
                    pizza.getPizzaId()
            );

            cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setPizzaId(pizza.getPizzaId());
            cartItem.setPizzaName(pizza.getPizzaName());
            cartItem.setPrice(pizza.getPrice());
            cartItem.setQuantity(request.getQuantity());
        }

        cartItem.setTotalPrice(cartItem.getPrice() * cartItem.getQuantity());

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        log.info("Cart item saved successfully. cartItemId: {}, userId: {}, pizzaId: {}, quantity: {}, totalPrice: {}",
                savedCartItem.getCartItemId(),
                savedCartItem.getUserId(),
                savedCartItem.getPizzaId(),
                savedCartItem.getQuantity(),
                savedCartItem.getTotalPrice()
        );

        return convertToResponse(savedCartItem);
    }

    @Override
    public List<CartItemResponse> getCartByUser(Long userId) {

        log.info("Fetching cart items for userId: {}", userId);

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        List<CartItemResponse> responses = new ArrayList<>();

        for (CartItem item : cartItems) {
            responses.add(convertToResponse(item));
        }

        log.info("Cart items fetched successfully. userId: {}, count: {}", userId, responses.size());

        return responses;
    }

    @Override
    public CartItemResponse updateCartItem(Long cartItemId, Integer quantity) {

        log.info("Update cart item request received. cartItemId: {}, newQuantity: {}",
                cartItemId,
                quantity
        );

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {
                    log.warn("Update cart item failed. Cart item not found. cartItemId: {}", cartItemId);
                    return new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
                });

        log.info("Cart item found. cartItemId: {}, oldQuantity: {}",
                cartItem.getCartItemId(),
                cartItem.getQuantity()
        );

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getPrice() * quantity);

        CartItem updatedCartItem = cartItemRepository.save(cartItem);

        log.info("Cart item updated successfully. cartItemId: {}, quantity: {}, totalPrice: {}",
                updatedCartItem.getCartItemId(),
                updatedCartItem.getQuantity(),
                updatedCartItem.getTotalPrice()
        );

        return convertToResponse(updatedCartItem);
    }

    @Override
    public void removeCartItem(Long cartItemId) {

        log.info("Remove cart item request received. cartItemId: {}", cartItemId);

        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> {
                    log.warn("Remove cart item failed. Cart item not found. cartItemId: {}", cartItemId);
                    return new ResourceNotFoundException("Cart item not found with id: " + cartItemId);
                });

        cartItemRepository.delete(cartItem);

        log.info("Cart item removed successfully. cartItemId: {}", cartItemId);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {

        log.info("Clear cart request received. userId: {}", userId);

        cartItemRepository.deleteByUserId(userId);

        log.info("Cart cleared successfully. userId: {}", userId);
    }

    private CartItemResponse convertToResponse(CartItem cartItem) {

        CartItemResponse response = new CartItemResponse();

        response.setCartItemId(cartItem.getCartItemId());
        response.setUserId(cartItem.getUserId());
        response.setPizzaId(cartItem.getPizzaId());
        response.setPizzaName(cartItem.getPizzaName());
        response.setPrice(cartItem.getPrice());
        response.setQuantity(cartItem.getQuantity());
        response.setTotalPrice(cartItem.getTotalPrice());

        return response;
    }
}