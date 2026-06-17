package com.smartpizza.authservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPartnerRequest {

    private Long authUserId;
    private String partnerName;
    private String mobileNumber;
    private String email;
    private String city;
}