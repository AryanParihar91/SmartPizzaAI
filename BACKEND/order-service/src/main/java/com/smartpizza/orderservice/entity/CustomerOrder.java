package com.smartpizza.orderservice.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.smartpizza.orderservice.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "customer_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;

	@Column(nullable = false)
	private Long userId;

	private Double subtotal;

	private Double discountAmount;

	private Double totalAmount;

	private String couponCode;

	private String deliveryAddress;
	
	private String deliveryCity;

	private String customerMobile;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private OrderStatus orderStatus;

	private LocalDateTime orderDate;

	@OneToMany(mappedBy = "customerOrder", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems;

}
