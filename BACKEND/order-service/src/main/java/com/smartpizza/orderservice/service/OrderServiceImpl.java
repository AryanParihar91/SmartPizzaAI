package com.smartpizza.orderservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.smartpizza.orderservice.dto.OrderItemResponse;
import com.smartpizza.orderservice.dto.OrderResponse;
import com.smartpizza.orderservice.dto.PlaceOrderRequest;
import com.smartpizza.orderservice.entity.CartItem;
import com.smartpizza.orderservice.entity.Coupon;
import com.smartpizza.orderservice.entity.CustomerOrder;
import com.smartpizza.orderservice.entity.DeliveryPartner;
import com.smartpizza.orderservice.entity.DeliveryTracking;
import com.smartpizza.orderservice.entity.OrderItem;
import com.smartpizza.orderservice.enums.DeliveryStatus;
import com.smartpizza.orderservice.enums.OrderStatus;
import com.smartpizza.orderservice.exception.ResourceNotFoundException;
import com.smartpizza.orderservice.repository.CartItemRepository;
import com.smartpizza.orderservice.repository.CouponRepository;
import com.smartpizza.orderservice.repository.CustomerOrderRepository;
import com.smartpizza.orderservice.repository.DeliveryPartnerRepository;
import com.smartpizza.orderservice.repository.DeliveryTrackingRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartItemRepository cartItemRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final CouponRepository couponRepository;
    private final DeliveryTrackingRepository deliveryTrackingRepository;
    private final DeliveryPartnerRepository deliveryPartnerRepository;

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {

        log.info("Placing order request received for userId: {}", userId);

        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);

        if (cartItems.isEmpty()) {
            log.warn("Order placement failed. Cart is empty for userId: {}", userId);
            throw new RuntimeException("Cart is empty. Add pizzas before placing order.");
        }

        log.info("Cart items found for userId {}: {}", userId, cartItems.size());

        double subtotal = 0;

        for (CartItem cartItem : cartItems) {
            subtotal = subtotal + cartItem.getTotalPrice();
        }

        log.info("Calculated subtotal for userId {}: {}", userId, subtotal);

        double discountAmount = 0;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {

            log.info("Coupon code applied by userId {}: {}", userId, request.getCouponCode());

            Coupon coupon = couponRepository.findByCouponCode(request.getCouponCode())
                    .orElseThrow(() -> {
                        log.warn("Coupon not found. userId: {}, couponCode: {}", userId, request.getCouponCode());
                        return new ResourceNotFoundException("Coupon not found: " + request.getCouponCode());
                    });

            if (coupon.getActive() != null && !coupon.getActive()) {
                log.warn("Inactive coupon used. userId: {}, couponCode: {}", userId, request.getCouponCode());
                throw new RuntimeException("Coupon is not active");
            }

            if (coupon.getMinimumOrderAmount() != null && subtotal < coupon.getMinimumOrderAmount()) {
                log.warn(
                        "Coupon minimum amount not met. userId: {}, subtotal: {}, requiredAmount: {}",
                        userId,
                        subtotal,
                        coupon.getMinimumOrderAmount()
                );

                throw new RuntimeException("Minimum order amount required: " + coupon.getMinimumOrderAmount());
            }

            discountAmount = subtotal * coupon.getDiscountPercentage() / 100;

            log.info(
                    "Coupon applied successfully. userId: {}, couponCode: {}, discountAmount: {}",
                    userId,
                    coupon.getCouponCode(),
                    discountAmount
            );
        }

        double totalAmount = subtotal - discountAmount;

        log.info("Final order amount calculated for userId {}: {}", userId, totalAmount);

        CustomerOrder order = new CustomerOrder();
        order.setUserId(userId);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmount(totalAmount);
        order.setCouponCode(request.getCouponCode());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setDeliveryCity("Bengaluru");
        order.setCustomerMobile(request.getCustomerMobile());
        order.setOrderStatus(OrderStatus.PLACED);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();

            orderItem.setPizzaId(cartItem.getPizzaId());
            orderItem.setPizzaName(cartItem.getPizzaName());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(cartItem.getTotalPrice());
            orderItem.setCustomerOrder(order);

            orderItems.add(orderItem);
        }

        order.setOrderItems(orderItems);

        CustomerOrder savedOrder = customerOrderRepository.save(order);

        log.info(
                "Order created successfully. orderId: {}, userId: {}, totalAmount: {}",
                savedOrder.getOrderId(),
                userId,
                savedOrder.getTotalAmount()
        );

        DeliveryTracking tracking = new DeliveryTracking();
        tracking.setOrderId(savedOrder.getOrderId());

        DeliveryPartner partner = deliveryPartnerRepository
                .findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru")
                .orElseThrow(() -> {
                    log.warn(
                            "No available delivery partner found in Bengaluru for orderId: {}",
                            savedOrder.getOrderId()
                    );

                    return new RuntimeException("No delivery partner available in Bengaluru");
                });

        tracking.setDeliveryPartnerId(partner.getPartnerId());
        tracking.setDeliveryPartnerName(partner.getPartnerName());
        tracking.setDeliveryPartnerMobile(partner.getMobileNumber());
        tracking.setDeliveryStatus(DeliveryStatus.ASSIGNED);
        tracking.setEtaMinutes(45);

        partner.setAvailable(false);
        deliveryPartnerRepository.save(partner);

        savedOrder.setOrderStatus(OrderStatus.ASSIGNED);
        customerOrderRepository.save(savedOrder);

        deliveryTrackingRepository.save(tracking);

        log.info(
                "Delivery partner auto-assigned. orderId: {}, partnerId: {}, partnerName: {}",
                savedOrder.getOrderId(),
                partner.getPartnerId(),
                partner.getPartnerName()
        );

        cartItemRepository.deleteByUserId(userId);

        log.info("Cart cleared after order placement. userId: {}", userId);

        log.info(
                "Order placement completed successfully. orderId: {}, userId: {}",
                savedOrder.getOrderId(),
                userId
        );

        return convertToResponse(savedOrder);
    }

    @Override
    public List<OrderResponse> getOrdersByUser(Long userId) {

        log.info("Fetching orders for userId: {}", userId);

        List<CustomerOrder> orders = customerOrderRepository.findByUserId(userId);
        List<OrderResponse> responses = new ArrayList<>();

        for (CustomerOrder order : orders) {
            responses.add(convertToResponse(order));
        }

        log.info("Orders fetched successfully for userId: {}, count: {}", userId, responses.size());

        return responses;
    }

    @Override
    public OrderResponse getOrderById(Long orderId) {

        log.info("Fetching order by orderId: {}", orderId);

        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order not found with orderId: {}", orderId);
                    return new ResourceNotFoundException("Order not found with id: " + orderId);
                });

        log.info("Order fetched successfully. orderId: {}", orderId);

        return convertToResponse(order);
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {

        log.info("Updating order status. orderId: {}, newStatus: {}", orderId, status);

        CustomerOrder order = customerOrderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order status update failed. Order not found with orderId: {}", orderId);
                    return new ResourceNotFoundException("Order not found with id: " + orderId);
                });

        order.setOrderStatus(status);

        CustomerOrder updatedOrder = customerOrderRepository.save(order);

        log.info("Order status updated successfully. orderId: {}, status: {}", orderId, status);

        return convertToResponse(updatedOrder);
    }

    @Override
    public List<OrderResponse> getAllOrders() {

        log.info("Fetching all orders");

        List<CustomerOrder> orders = customerOrderRepository.findAll();
        List<OrderResponse> responses = new ArrayList<>();

        for (CustomerOrder order : orders) {
            responses.add(convertToResponse(order));
        }

        log.info("All orders fetched successfully. count: {}", responses.size());

        return responses;
    }

    @Override
    public List<Long> getTopOrderedPizzaIdsByUser(Long userId) {

        log.info("Fetching top ordered pizza ids for userId: {}", userId);

        List<CustomerOrder> orders = customerOrderRepository.findByUserIdOrderByOrderDateDesc(userId);

        Map<Long, Integer> pizzaCountMap = new HashMap<>();

        for (CustomerOrder order : orders) {
            if (order.getOrderItems() != null) {
                for (OrderItem item : order.getOrderItems()) {
                    Long pizzaId = item.getPizzaId();
                    Integer quantity = item.getQuantity();

                    if (pizzaId != null && quantity != null) {
                        pizzaCountMap.put(
                                pizzaId,
                                pizzaCountMap.getOrDefault(pizzaId, 0) + quantity
                        );
                    }
                }
            }
        }

        List<Long> topPizzaIds = pizzaCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue(Comparator.reverseOrder()))
                .limit(4)
                .map(Map.Entry::getKey)
                .toList();

        log.info("Top ordered pizza ids for userId {}: {}", userId, topPizzaIds);

        return topPizzaIds;
    }

    private OrderResponse convertToResponse(CustomerOrder order) {

        OrderResponse response = new OrderResponse();

        response.setOrderId(order.getOrderId());
        response.setUserId(order.getUserId());
        response.setSubtotal(order.getSubtotal());
        response.setDiscountAmount(order.getDiscountAmount());
        response.setTotalAmount(order.getTotalAmount());
        response.setCouponCode(order.getCouponCode());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setDeliveryCity(order.getDeliveryCity());
        response.setCustomerMobile(order.getCustomerMobile());
        response.setOrderStatus(order.getOrderStatus());
        response.setOrderDate(order.getOrderDate());

        List<OrderItemResponse> itemResponses = new ArrayList<>();

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                OrderItemResponse itemResponse = new OrderItemResponse();

                itemResponse.setOrderItemId(item.getOrderItemId());
                itemResponse.setPizzaId(item.getPizzaId());
                itemResponse.setPizzaName(item.getPizzaName());
                itemResponse.setPrice(item.getPrice());
                itemResponse.setQuantity(item.getQuantity());
                itemResponse.setTotalPrice(item.getTotalPrice());

                itemResponses.add(itemResponse);
            }
        }

        response.setOrderItems(itemResponses);

        return response;
    }
}