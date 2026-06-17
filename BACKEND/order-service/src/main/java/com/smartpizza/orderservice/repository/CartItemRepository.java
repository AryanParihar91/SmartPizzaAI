package com.smartpizza.orderservice.repository;

import com.smartpizza.orderservice.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    Optional<CartItem> findByUserIdAndPizzaId(Long userId, Long pizzaId);

    void deleteByUserId(Long userId);
}