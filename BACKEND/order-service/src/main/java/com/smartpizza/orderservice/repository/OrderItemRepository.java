package com.smartpizza.orderservice.repository;

import com.smartpizza.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByCustomerOrderOrderId(Long orderId);
}
