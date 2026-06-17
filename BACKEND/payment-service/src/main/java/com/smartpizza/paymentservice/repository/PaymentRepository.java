package com.smartpizza.paymentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartpizza.paymentservice.entity.Payment;
import com.smartpizza.paymentservice.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	List<Payment> findByUserId(Long userId);

	Optional<Payment> findByOrderId(Long orderId);

	Optional<Payment> findFirstByOrderIdAndPaymentStatusOrderByPaymentIdDesc(Long orderId, PaymentStatus paymentStatus);

}