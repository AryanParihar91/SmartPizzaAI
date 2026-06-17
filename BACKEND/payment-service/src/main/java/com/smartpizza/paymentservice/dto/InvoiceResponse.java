package com.smartpizza.paymentservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class InvoiceResponse {

    private Long invoiceId;
    private Long paymentId;
    private Long orderId;
    private Long userId;
    private Double subtotal;
    private Double gstAmount;
    private Double totalAmount;
    private String invoiceNumber;
    private LocalDateTime invoiceDate;
}
