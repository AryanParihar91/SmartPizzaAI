package com.smartpizza.orderservice.service;

import com.smartpizza.orderservice.client.MenuClient;
import com.smartpizza.orderservice.dto.AddCartItemRequest;
import com.smartpizza.orderservice.dto.CartItemResponse;
import com.smartpizza.orderservice.dto.MenuPizzaResponse;
import com.smartpizza.orderservice.entity.CartItem;
import com.smartpizza.orderservice.exception.ResourceNotFoundException;
import com.smartpizza.orderservice.repository.CartItemRepository;

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
class CartServiceImplTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private MenuClient menuClient;

    @InjectMocks
    private CartServiceImpl cartService;

    private MenuPizzaResponse pizzaResponse;
    private AddCartItemRequest addCartItemRequest;
    private CartItem cartItem;

    @BeforeEach
    void setup() {

        pizzaResponse = new MenuPizzaResponse();
        pizzaResponse.setPizzaId(1L);
        pizzaResponse.setPizzaName("Farmhouse Pizza");
        pizzaResponse.setPrice(349.0);
        pizzaResponse.setAvailable(true);
        pizzaResponse.setVeg(true);
        pizzaResponse.setPreparationTimeMinutes(20);

        addCartItemRequest = new AddCartItemRequest();
        addCartItemRequest.setPizzaId(1L);
        addCartItemRequest.setQuantity(2);

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
    void addToCartWhenNewPizzaShouldAddCartItem() {

        when(menuClient.getPizzaById(1L)).thenReturn(pizzaResponse);
        when(cartItemRepository.findByUserIdAndPizzaId(1L, 1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponse response = cartService.addToCart(1L, addCartItemRequest);

        assertNotNull(response);
        assertEquals(1L, response.getCartItemId());
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getPizzaId());
        assertEquals("Farmhouse Pizza", response.getPizzaName());
        assertEquals(349.0, response.getPrice());
        assertEquals(2, response.getQuantity());
        assertEquals(698.0, response.getTotalPrice());

        verify(menuClient, times(1)).getPizzaById(1L);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addToCartWhenPizzaAlreadyExistsShouldIncreaseQuantity() {

        CartItem existingCartItem = new CartItem();
        existingCartItem.setCartItemId(1L);
        existingCartItem.setUserId(1L);
        existingCartItem.setPizzaId(1L);
        existingCartItem.setPizzaName("Farmhouse Pizza");
        existingCartItem.setPrice(349.0);
        existingCartItem.setQuantity(1);
        existingCartItem.setTotalPrice(349.0);

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setCartItemId(1L);
        updatedCartItem.setUserId(1L);
        updatedCartItem.setPizzaId(1L);
        updatedCartItem.setPizzaName("Farmhouse Pizza");
        updatedCartItem.setPrice(349.0);
        updatedCartItem.setQuantity(3);
        updatedCartItem.setTotalPrice(1047.0);

        when(menuClient.getPizzaById(1L)).thenReturn(pizzaResponse);
        when(cartItemRepository.findByUserIdAndPizzaId(1L, 1L)).thenReturn(Optional.of(existingCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(updatedCartItem);

        CartItemResponse response = cartService.addToCart(1L, addCartItemRequest);

        assertNotNull(response);
        assertEquals(3, response.getQuantity());
        assertEquals(1047.0, response.getTotalPrice());

        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void addToCartWhenPizzaNotAvailableShouldThrowException() {

        pizzaResponse.setAvailable(false);

        when(menuClient.getPizzaById(1L)).thenReturn(pizzaResponse);

        assertThrows(RuntimeException.class, () -> {
            cartService.addToCart(1L, addCartItemRequest);
        });

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void getCartByUserShouldReturnCartItems() {

        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

        List<CartItemResponse> responses = cartService.getCartByUser(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Farmhouse Pizza", responses.get(0).getPizzaName());

        verify(cartItemRepository, times(1)).findByUserId(1L);
    }

    @Test
    void updateCartItemWhenCartItemExistsShouldUpdateQuantity() {

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setCartItemId(1L);
        updatedCartItem.setUserId(1L);
        updatedCartItem.setPizzaId(1L);
        updatedCartItem.setPizzaName("Farmhouse Pizza");
        updatedCartItem.setPrice(349.0);
        updatedCartItem.setQuantity(3);
        updatedCartItem.setTotalPrice(1047.0);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(updatedCartItem);

        CartItemResponse response = cartService.updateCartItem(1L, 3);

        assertNotNull(response);
        assertEquals(3, response.getQuantity());
        assertEquals(1047.0, response.getTotalPrice());

        verify(cartItemRepository, times(1)).findById(1L);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void updateCartItemWhenCartItemNotFoundShouldThrowException() {

        when(cartItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            cartService.updateCartItem(99L, 3);
        });

        verify(cartItemRepository, never()).save(any(CartItem.class));
    }

    @Test
    void removeCartItemShouldDeleteCartItem() {

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartService.removeCartItem(1L);

        verify(cartItemRepository, times(1)).findById(1L);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void clearCartShouldDeleteCartByUserId() {

        cartService.clearCart(1L);

        verify(cartItemRepository, times(1)).deleteByUserId(1L);
    }
}