package com.smartpizza.paymentservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.smartpizza.paymentservice.dto.InvoiceResponse;
import com.smartpizza.paymentservice.dto.PaymentRequest;
import com.smartpizza.paymentservice.dto.PaymentResponse;
import com.smartpizza.paymentservice.entity.Invoice;
import com.smartpizza.paymentservice.entity.Payment;
import com.smartpizza.paymentservice.enums.PaymentMode;
import com.smartpizza.paymentservice.enums.PaymentStatus;
import com.smartpizza.paymentservice.exception.ResourceNotFoundException;
import com.smartpizza.paymentservice.repository.InvoiceRepository;
import com.smartpizza.paymentservice.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final InvoiceRepository invoiceRepository;

	@Override
	public PaymentResponse makePayment(Long userId, PaymentRequest request) {

		Payment existingSuccessPayment = paymentRepository
				.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(request.getOrderId(), PaymentStatus.SUCCESS)
				.orElse(null);

		if (existingSuccessPayment != null) {
			throw new RuntimeException("Successful payment already exists for order id: " + request.getOrderId());
		}

		Payment existingCodPayment = paymentRepository
				.findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(request.getOrderId(), PaymentStatus.PENDING)
				.orElse(null);

		if (existingCodPayment != null) {
			throw new RuntimeException("COD payment already exists for order id: " + request.getOrderId());
		}

		validatePaymentMode(request);

		double subtotal = request.getAmount();
		double gstAmount = calculateGst(subtotal);
		double totalAmount = subtotal + gstAmount;

		Payment payment = new Payment();
		payment.setOrderId(request.getOrderId());
		payment.setUserId(userId);
		payment.setSubtotal(subtotal);
		payment.setGstAmount(gstAmount);
		payment.setTotalAmount(totalAmount);
		payment.setPaymentMode(request.getPaymentMode());
		payment.setPaymentDate(LocalDateTime.now());

		if (request.getPaymentMode() == PaymentMode.COD) {

			payment.setPaymentStatus(PaymentStatus.PENDING);
			payment.setTransactionId("COD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

		} else {

			if (request.getSimulateFailure() != null && request.getSimulateFailure()) {
				payment.setPaymentStatus(PaymentStatus.FAILED);
				payment.setTransactionId("FAILED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
			} else {
				payment.setPaymentStatus(PaymentStatus.SUCCESS);
				payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
			}
		}

		Payment savedPayment = paymentRepository.save(payment);

		if (savedPayment.getPaymentStatus() == PaymentStatus.SUCCESS
				|| savedPayment.getPaymentStatus() == PaymentStatus.PENDING) {

			if (invoiceRepository.findByOrderId(savedPayment.getOrderId()).isEmpty()) {
				Invoice invoice = createInvoice(savedPayment);
				invoiceRepository.save(invoice);
			}
		}

		return convertToPaymentResponse(savedPayment);
	}

	@Override
	public PaymentResponse getPaymentByOrderId(Long orderId) {

		Payment payment = paymentRepository.findByOrderId(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Payment not found for order id: " + orderId));

		return convertToPaymentResponse(payment);
	}

	@Override
	public List<PaymentResponse> getPaymentsByUser(Long userId) {

		List<Payment> payments = paymentRepository.findByUserId(userId);
		List<PaymentResponse> responses = new ArrayList<>();

		for (Payment payment : payments) {
			responses.add(convertToPaymentResponse(payment));
		}

		return responses;
	}

	@Override
	public InvoiceResponse getInvoiceByOrderId(Long orderId) {

		Invoice invoice = invoiceRepository.findByOrderId(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found for order id: " + orderId));

		return convertToInvoiceResponse(invoice);
	}

	@Override
	public InvoiceResponse getInvoiceByPaymentId(Long paymentId) {

		Invoice invoice = invoiceRepository.findByPaymentId(paymentId)
				.orElseThrow(() -> new ResourceNotFoundException("Invoice not found for payment id: " + paymentId));

		return convertToInvoiceResponse(invoice);
	}

	private void validatePaymentMode(PaymentRequest request) {

		if (request.getPaymentMode() == PaymentMode.UPI) {
			if (request.getUpiId() == null || request.getUpiId().isBlank()) {
				throw new RuntimeException("UPI id is required for UPI payment");
			}
		}

		if (request.getPaymentMode() == PaymentMode.CARD) {
			if (request.getCardNumber() == null || request.getCardNumber().isBlank()) {
				throw new RuntimeException("Card number is required for card payment");
			}

			if (request.getCardHolderName() == null || request.getCardHolderName().isBlank()) {
				throw new RuntimeException("Card holder name is required for card payment");
			}
		}
	}

	private double calculateGst(double amount) {
		return amount * 0.05;
	}

	private Invoice createInvoice(Payment payment) {

		Invoice invoice = new Invoice();

		invoice.setPaymentId(payment.getPaymentId());
		invoice.setOrderId(payment.getOrderId());
		invoice.setUserId(payment.getUserId());
		invoice.setSubtotal(payment.getSubtotal());
		invoice.setGstAmount(payment.getGstAmount());
		invoice.setTotalAmount(payment.getTotalAmount());
		invoice.setInvoiceNumber(
				"INV-" + payment.getOrderId() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
		invoice.setInvoiceDate(LocalDateTime.now());

		return invoice;
	}

	private PaymentResponse convertToPaymentResponse(Payment payment) {

		PaymentResponse response = new PaymentResponse();

		response.setPaymentId(payment.getPaymentId());
		response.setOrderId(payment.getOrderId());
		response.setUserId(payment.getUserId());
		response.setSubtotal(payment.getSubtotal());
		response.setGstAmount(payment.getGstAmount());
		response.setTotalAmount(payment.getTotalAmount());
		response.setPaymentMode(payment.getPaymentMode());
		response.setPaymentStatus(payment.getPaymentStatus());
		response.setTransactionId(payment.getTransactionId());
		response.setPaymentDate(payment.getPaymentDate());

		if (payment.getPaymentMode() == PaymentMode.COD) {
			response.setMessage("COD order placed. Payment will be collected on delivery.");
		} else {
			response.setMessage("Payment completed successfully.");
		}

		return response;
	}

	private InvoiceResponse convertToInvoiceResponse(Invoice invoice) {

		InvoiceResponse response = new InvoiceResponse();

		response.setInvoiceId(invoice.getInvoiceId());
		response.setPaymentId(invoice.getPaymentId());
		response.setOrderId(invoice.getOrderId());
		response.setUserId(invoice.getUserId());
		response.setSubtotal(invoice.getSubtotal());
		response.setGstAmount(invoice.getGstAmount());
		response.setTotalAmount(invoice.getTotalAmount());
		response.setInvoiceNumber(invoice.getInvoiceNumber());
		response.setInvoiceDate(invoice.getInvoiceDate());

		return response;
	}
}
