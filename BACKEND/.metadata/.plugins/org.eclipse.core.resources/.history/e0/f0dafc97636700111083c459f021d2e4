package com.smartpizza.orderservice.entity;

import com.smartpizza.orderservice.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "delivery_tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackingId;

    @Column(nullable = false)
    private Long orderId;

    private Long deliveryPartnerId;

    private String deliveryPartnerName;

    private String deliveryPartnerMobile;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    private Integer etaMinutes;
}