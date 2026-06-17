package com.smartpizza.paymentservice.dto;

import com.smartpizza.paymentservice.enums.PaymentMode;
import com.smartpizza.paymentservice.enums.PaymentStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentResponse {

    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Double subtotal;
    private Double gstAmount;
    private Double totalAmount;
    private PaymentMode paymentMode;
    private PaymentStatus paymentStatus;
    private String transactionId;
    private LocalDateTime paymentDate;
    private String message;
}