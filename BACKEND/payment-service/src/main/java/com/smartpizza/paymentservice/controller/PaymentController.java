package com.smartpizza.paymentservice.controller;

import com.smartpizza.paymentservice.dto.PaymentRequest;
import com.smartpizza.paymentservice.dto.PaymentResponse;
import com.smartpizza.paymentservice.service.PaymentService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public PaymentResponse makePayment(@RequestHeader("X-User-Id") Long userId,
                                       @Valid @RequestBody PaymentRequest request) {
        return paymentService.makePayment(userId, request);
    }

    @GetMapping("/order/{orderId}")
    public PaymentResponse getPaymentByOrderId(@PathVariable Long orderId) {
        return paymentService.getPaymentByOrderId(orderId);
    }

    @GetMapping("/my-payments")
    public List<PaymentResponse> getMyPayments(@RequestHeader("X-User-Id") Long userId) {
        return paymentService.getPaymentsByUser(userId);
    }

    @GetMapping("/test")
    public String test() {
        return "Payment service is working";
    }
}