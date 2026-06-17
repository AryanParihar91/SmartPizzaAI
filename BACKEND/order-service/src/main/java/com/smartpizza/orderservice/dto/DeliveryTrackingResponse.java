package com.smartpizza.orderservice.dto;

import com.smartpizza.orderservice.enums.DeliveryStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryTrackingResponse {

    private Long trackingId;
    private Long orderId;
    private Long deliveryPartnerId;
    private String deliveryPartnerName;
    private String deliveryPartnerMobile;
    private DeliveryStatus deliveryStatus;
    private Integer etaMinutes;
}