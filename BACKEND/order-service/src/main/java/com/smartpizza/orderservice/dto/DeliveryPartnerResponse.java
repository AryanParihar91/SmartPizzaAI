package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPartnerResponse {

    private Long partnerId;
    private Long authUserId;
    private String partnerName;
    private String mobileNumber;
    private String email;
    private String city;
    private Boolean available;
}