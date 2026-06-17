package com.smartpizza.orderservice.service;

import java.util.List;

import com.smartpizza.orderservice.dto.DeliveryPartnerRequest;
import com.smartpizza.orderservice.dto.DeliveryPartnerResponse;
import com.smartpizza.orderservice.dto.DeliveryTrackingResponse;
import com.smartpizza.orderservice.enums.DeliveryStatus;

public interface DeliveryService {

	DeliveryPartnerResponse createDeliveryPartner(DeliveryPartnerRequest request);

	List<DeliveryPartnerResponse> getAllDeliveryPartners();

	DeliveryTrackingResponse trackOrder(Long orderId);

	DeliveryTrackingResponse trackLatestUndeliveredOrder(Long userId);

	DeliveryTrackingResponse getMyAssignedDelivery(Long authUserId);

	DeliveryTrackingResponse updateDeliveryStatus(Long orderId, DeliveryStatus status);
}