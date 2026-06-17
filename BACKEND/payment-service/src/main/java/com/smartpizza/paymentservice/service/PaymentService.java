package com.smartpizza.paymentservice.service;

import com.smartpizza.paymentservice.dto.InvoiceResponse;
import com.smartpizza.paymentservice.dto.PaymentRequest;
import com.smartpizza.paymentservice.dto.PaymentResponse;

import java.util.List;

public interface PaymentService {

    PaymentResponse makePayment(Long userId, PaymentRequest request);

    PaymentResponse getPaymentByOrderId(Long orderId);

    List<PaymentResponse> getPaymentsByUser(Long userId);

    InvoiceResponse getInvoiceByOrderId(Long orderId);

    InvoiceResponse getInvoiceByPaymentId(Long paymentId);
}
