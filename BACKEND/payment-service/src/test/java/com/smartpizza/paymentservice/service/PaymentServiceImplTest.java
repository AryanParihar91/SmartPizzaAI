package com.smartpizza.paymentservice.service;

import com.smartpizza.paymentservice.dto.PaymentRequest;
import com.smartpizza.paymentservice.dto.PaymentResponse;
import com.smartpizza.paymentservice.entity.Invoice;
import com.smartpizza.paymentservice.entity.Payment;
import com.smartpizza.paymentservice.enums.PaymentMode;
import com.smartpizza.paymentservice.enums.PaymentStatus;
import com.smartpizza.paymentservice.repository.InvoiceRepository;
import com.smartpizza.paymentservice.repository.PaymentRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private InvoiceRepository invoiceRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    void makePaymentWhenCardPaymentSuccessShouldReturnSuccessAndCreateInvoice() {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(600.0);
        request.setPaymentMode(PaymentMode.CARD);
        request.setCardNumber("4111111111111111");
        request.setCardHolderName("Aryan Parihar");
        request.setSimulateFailure(false);

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(1L, PaymentStatus.SUCCESS))
                .thenReturn(Optional.empty());

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(1L, PaymentStatus.PENDING))
                .thenReturn(Optional.empty());

        when(invoiceRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(1L);
            return payment;
        });

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.makePayment(1L, request);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(1L, response.getOrderId());
        assertEquals(PaymentMode.CARD, response.getPaymentMode());
        assertEquals(PaymentStatus.SUCCESS, response.getPaymentStatus());
        assertEquals(600.0, response.getSubtotal());
        assertEquals(30.0, response.getGstAmount());
        assertEquals(630.0, response.getTotalAmount());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void makePaymentWhenUpiPaymentFailsShouldNotCreateInvoice() {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(600.0);
        request.setPaymentMode(PaymentMode.UPI);
        request.setUpiId("aryan@upi");
        request.setSimulateFailure(true);

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(1L, PaymentStatus.SUCCESS))
                .thenReturn(Optional.empty());

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(1L, PaymentStatus.PENDING))
                .thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(1L);
            return payment;
        });

        PaymentResponse response = paymentService.makePayment(1L, request);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.getPaymentStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(invoiceRepository, never()).save(any(Invoice.class));
    }

    @Test
    void makePaymentWhenCodShouldReturnPendingAndCreateInvoice() {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(2L);
        request.setAmount(500.0);
        request.setPaymentMode(PaymentMode.COD);

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(2L, PaymentStatus.SUCCESS))
                .thenReturn(Optional.empty());

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(2L, PaymentStatus.PENDING))
                .thenReturn(Optional.empty());

        when(invoiceRepository.findByOrderId(2L)).thenReturn(Optional.empty());

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment payment = invocation.getArgument(0);
            payment.setPaymentId(2L);
            return payment;
        });

        when(invoiceRepository.save(any(Invoice.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.makePayment(1L, request);

        assertNotNull(response);
        assertEquals(PaymentMode.COD, response.getPaymentMode());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());

        verify(invoiceRepository, times(1)).save(any(Invoice.class));
    }

    @Test
    void makePaymentWhenSuccessfulPaymentAlreadyExistsShouldThrowException() {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(1L);
        request.setAmount(600.0);
        request.setPaymentMode(PaymentMode.UPI);
        request.setUpiId("aryan@upi");

        Payment existingPayment = new Payment();
        existingPayment.setPaymentId(10L);
        existingPayment.setOrderId(1L);
        existingPayment.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(1L, PaymentStatus.SUCCESS))
                .thenReturn(Optional.of(existingPayment));

        assertThrows(RuntimeException.class, () -> {
            paymentService.makePayment(1L, request);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void makePaymentWhenUpiIdMissingShouldThrowException() {

        PaymentRequest request = new PaymentRequest();
        request.setOrderId(3L);
        request.setAmount(600.0);
        request.setPaymentMode(PaymentMode.UPI);

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(3L, PaymentStatus.SUCCESS))
                .thenReturn(Optional.empty());

        when(paymentRepository.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(3L, PaymentStatus.PENDING))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            paymentService.makePayment(1L, request);
        });

        verify(paymentRepository, never()).save(any(Payment.class));
    }
}
