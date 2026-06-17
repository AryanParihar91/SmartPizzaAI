package com.smartpizza.paymentservice.repository;

import com.smartpizza.paymentservice.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByOrderId(Long orderId);

    Optional<Invoice> findByPaymentId(Long paymentId);
}
