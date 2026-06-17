package com.smartpizza.orderservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.smartpizza.orderservice.dto.OrderResponse;
import com.smartpizza.orderservice.dto.PlaceOrderRequest;
import com.smartpizza.orderservice.enums.OrderStatus;
import com.smartpizza.orderservice.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // place order api
    @PostMapping("/place")
    public OrderResponse placeOrder(@RequestHeader("X-User-Id") Long userId,
                                    @Valid @RequestBody PlaceOrderRequest request) {
        return orderService.placeOrder(userId, request);
    }

    // get user orders
    @GetMapping("/my-orders")
    public List<OrderResponse> getMyOrders(@RequestHeader("X-User-Id") Long userId) {
        return orderService.getOrdersByUser(userId);
    }

    // get order by id
    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    // updating order status
    @PutMapping("/status/{orderId}")
    public OrderResponse updateOrderStatus(@PathVariable Long orderId,
                                           @RequestParam OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    // getting top 4 pizza recommendations
    @GetMapping("/recommendations/top-pizza-ids")
    public List<Long> getTopOrderedPizzaIdsByUser(@RequestHeader("X-User-Id") Long userId) {
        return orderService.getTopOrderedPizzaIdsByUser(userId);
    }
}