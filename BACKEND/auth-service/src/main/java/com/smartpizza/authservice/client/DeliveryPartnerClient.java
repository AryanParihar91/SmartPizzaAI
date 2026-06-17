package com.smartpizza.authservice.client;

import com.smartpizza.authservice.dto.DeliveryPartnerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "order-service")
public interface DeliveryPartnerClient {

    @PostMapping("/api/delivery/partners")
    void createDeliveryPartner(DeliveryPartnerRequest request);
}