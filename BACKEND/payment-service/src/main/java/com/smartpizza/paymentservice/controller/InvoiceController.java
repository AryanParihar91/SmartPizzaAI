package com.smartpizza.paymentservice.controller;

import com.smartpizza.paymentservice.dto.InvoiceResponse;
import com.smartpizza.paymentservice.service.PaymentService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public InvoiceResponse getInvoiceByOrderId(@PathVariable Long orderId) {
        return paymentService.getInvoiceByOrderId(orderId);
    }

    @GetMapping("/payment/{paymentId}")
    public InvoiceResponse getInvoiceByPaymentId(@PathVariable Long paymentId) {
        return paymentService.getInvoiceByPaymentId(paymentId);
    }
}
