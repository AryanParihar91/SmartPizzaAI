package com.smartpizza.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.smartpizza.orderservice.dto.DeliveryPartnerRequest;
import com.smartpizza.orderservice.dto.DeliveryPartnerResponse;
import com.smartpizza.orderservice.dto.DeliveryTrackingResponse;
import com.smartpizza.orderservice.entity.CustomerOrder;
import com.smartpizza.orderservice.entity.DeliveryPartner;
import com.smartpizza.orderservice.entity.DeliveryTracking;
import com.smartpizza.orderservice.enums.DeliveryStatus;
import com.smartpizza.orderservice.exception.ResourceNotFoundException;
import com.smartpizza.orderservice.repository.CustomerOrderRepository;
import com.smartpizza.orderservice.repository.DeliveryPartnerRepository;
import com.smartpizza.orderservice.repository.DeliveryTrackingRepository;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceImplTest {

	@Mock
	private DeliveryTrackingRepository deliveryTrackingRepository;

	@Mock
	private DeliveryPartnerRepository deliveryPartnerRepository;

	@Mock
	private CustomerOrderRepository customerOrderRepository;

	@InjectMocks
	private DeliveryServiceImpl deliveryService;

	private DeliveryPartner partner;
	private DeliveryTracking tracking;
	private CustomerOrder order;
	private DeliveryPartnerRequest partnerRequest;

	@BeforeEach
	void setup() {

		partner = new DeliveryPartner();
		partner.setPartnerId(1L);
		partner.setAuthUserId(10L);
		partner.setPartnerName("Rahul Kumar");
		partner.setMobileNumber("9876501234");
		partner.setEmail("rahul.delivery@gmail.com");
		partner.setCity("Bengaluru");
		partner.setAvailable(true);

		tracking = new DeliveryTracking();
		tracking.setTrackingId(1L);
		tracking.setOrderId(1L);
		tracking.setDeliveryPartnerId(null);
		tracking.setDeliveryPartnerName("Not Assigned");
		tracking.setDeliveryPartnerMobile("Not Assigned");
		tracking.setDeliveryStatus(DeliveryStatus.NOT_ASSIGNED);
		tracking.setEtaMinutes(40);

		order = new CustomerOrder();
		order.setOrderId(1L);
		order.setUserId(1L);
		order.setDeliveryAddress("Electronic City Phase 1");
		order.setDeliveryCity("Bengaluru");
		order.setCustomerMobile("9876543210");

		partnerRequest = new DeliveryPartnerRequest();
		partnerRequest.setAuthUserId(10L);
		partnerRequest.setPartnerName("Rahul Kumar");
		partnerRequest.setMobileNumber("9876501234");
		partnerRequest.setEmail("rahul.delivery@gmail.com");
		partnerRequest.setCity("Bengaluru");
	}

	@Test
	void createDeliveryPartnerShouldCreatePartnerSuccessfully() {

		when(deliveryPartnerRepository.existsByAuthUserId(10L)).thenReturn(false);
		when(deliveryPartnerRepository.existsByEmail("rahul.delivery@gmail.com")).thenReturn(false);
		when(deliveryPartnerRepository.existsByMobileNumber("9876501234")).thenReturn(false);
		when(deliveryPartnerRepository.save(any(DeliveryPartner.class))).thenReturn(partner);

		DeliveryPartnerResponse response = deliveryService.createDeliveryPartner(partnerRequest);

		assertNotNull(response);
		assertEquals(1L, response.getPartnerId());
		assertEquals(10L, response.getAuthUserId());
		assertEquals("Rahul Kumar", response.getPartnerName());
		assertEquals("9876501234", response.getMobileNumber());
		assertEquals("rahul.delivery@gmail.com", response.getEmail());
		assertEquals("Bengaluru", response.getCity());
		assertTrue(response.getAvailable());

		verify(deliveryPartnerRepository, times(1)).save(any(DeliveryPartner.class));
	}

	@Test
	void createDeliveryPartnerWhenAuthUserIdAlreadyExistsShouldThrowException() {

		when(deliveryPartnerRepository.existsByAuthUserId(10L)).thenReturn(true);

		assertThrows(RuntimeException.class, () -> {
			deliveryService.createDeliveryPartner(partnerRequest);
		});

		verify(deliveryPartnerRepository, never()).save(any(DeliveryPartner.class));
	}

	@Test
	void createDeliveryPartnerWhenEmailAlreadyExistsShouldThrowException() {

		when(deliveryPartnerRepository.existsByAuthUserId(10L)).thenReturn(false);
		when(deliveryPartnerRepository.existsByEmail("rahul.delivery@gmail.com")).thenReturn(true);

		assertThrows(RuntimeException.class, () -> {
			deliveryService.createDeliveryPartner(partnerRequest);
		});

		verify(deliveryPartnerRepository, never()).save(any(DeliveryPartner.class));
	}

	@Test
	void createDeliveryPartnerWhenMobileAlreadyExistsShouldThrowException() {

		when(deliveryPartnerRepository.existsByAuthUserId(10L)).thenReturn(false);
		when(deliveryPartnerRepository.existsByEmail("rahul.delivery@gmail.com")).thenReturn(false);
		when(deliveryPartnerRepository.existsByMobileNumber("9876501234")).thenReturn(true);

		assertThrows(RuntimeException.class, () -> {
			deliveryService.createDeliveryPartner(partnerRequest);
		});

		verify(deliveryPartnerRepository, never()).save(any(DeliveryPartner.class));
	}

	@Test
	void getAllDeliveryPartnersShouldReturnPartnersList() {

		DeliveryPartner partner2 = new DeliveryPartner();
		partner2.setPartnerId(2L);
		partner2.setAuthUserId(11L);
		partner2.setPartnerName("Amit Sharma");
		partner2.setMobileNumber("9876505678");
		partner2.setEmail("amit.delivery@gmail.com");
		partner2.setCity("Hyderabad");
		partner2.setAvailable(true);

		when(deliveryPartnerRepository.findAll()).thenReturn(List.of(partner, partner2));

		List<DeliveryPartnerResponse> responses = deliveryService.getAllDeliveryPartners();

		assertNotNull(responses);
		assertEquals(2, responses.size());
		assertEquals("Rahul Kumar", responses.get(0).getPartnerName());
		assertEquals("Bengaluru", responses.get(0).getCity());
		assertEquals("Amit Sharma", responses.get(1).getPartnerName());
		assertEquals("Hyderabad", responses.get(1).getCity());

		verify(deliveryPartnerRepository, times(1)).findAll();
	}

	@Test
	void trackOrderWhenTrackingExistsShouldReturnTrackingResponse() {

		tracking.setDeliveryPartnerId(1L);
		tracking.setDeliveryPartnerName("Rahul Kumar");
		tracking.setDeliveryPartnerMobile("9876501234");
		tracking.setDeliveryStatus(DeliveryStatus.ASSIGNED);
		tracking.setEtaMinutes(45);

		when(deliveryTrackingRepository.findByOrderId(1L)).thenReturn(Optional.of(tracking));

		DeliveryTrackingResponse response = deliveryService.trackOrder(1L);

		assertNotNull(response);
		assertEquals(1L, response.getTrackingId());
		assertEquals(1L, response.getOrderId());
		assertEquals(1L, response.getDeliveryPartnerId());
		assertEquals("Rahul Kumar", response.getDeliveryPartnerName());
		assertEquals("9876501234", response.getDeliveryPartnerMobile());
		assertEquals(DeliveryStatus.ASSIGNED, response.getDeliveryStatus());
		assertEquals(45, response.getEtaMinutes());

		verify(deliveryTrackingRepository, times(1)).findByOrderId(1L);
	}

	@Test
	void trackOrderWhenTrackingNotFoundShouldThrowException() {

		when(deliveryTrackingRepository.findByOrderId(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			deliveryService.trackOrder(99L);
		});

		verify(deliveryTrackingRepository, times(1)).findByOrderId(99L);
	}

	@Test
	void updateDeliveryStatus_whenAssigned_shouldAssignPartnerFromSameCity() {

		when(deliveryTrackingRepository.findByOrderId(1L)).thenReturn(Optional.of(tracking));
		when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(deliveryPartnerRepository.findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru"))
				.thenReturn(Optional.of(partner));

		when(deliveryPartnerRepository.save(any(DeliveryPartner.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(deliveryTrackingRepository.save(any(DeliveryTracking.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DeliveryTrackingResponse response = deliveryService.updateDeliveryStatus(1L, DeliveryStatus.ASSIGNED);

		assertNotNull(response);
		assertEquals(1L, response.getDeliveryPartnerId());
		assertEquals("Rahul Kumar", response.getDeliveryPartnerName());
		assertEquals("9876501234", response.getDeliveryPartnerMobile());
		assertEquals(DeliveryStatus.ASSIGNED, response.getDeliveryStatus());
		assertEquals(45, response.getEtaMinutes());

		assertFalse(partner.getAvailable());

		verify(customerOrderRepository, times(1)).findById(1L);
		verify(deliveryPartnerRepository, times(1)).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
		verify(deliveryPartnerRepository, times(1)).save(partner);
		verify(deliveryTrackingRepository, times(1)).save(any(DeliveryTracking.class));
	}

	@Test
	void updateDeliveryStatusWhenNoPartnerInCityShouldThrowException() {

		when(deliveryTrackingRepository.findByOrderId(1L)).thenReturn(Optional.of(tracking));
		when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(deliveryPartnerRepository.findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru"))
				.thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> {
			deliveryService.updateDeliveryStatus(1L, DeliveryStatus.ASSIGNED);
		});

		verify(deliveryPartnerRepository, never()).save(any(DeliveryPartner.class));
		verify(deliveryTrackingRepository, never()).save(any(DeliveryTracking.class));
	}

	@Test
	void updateDeliveryStatusWhenPickedUpShouldUpdateEta() {

		tracking.setDeliveryPartnerId(1L);
		tracking.setDeliveryPartnerName("Rahul Kumar");
		tracking.setDeliveryPartnerMobile("9876501234");

		when(deliveryTrackingRepository.findByOrderId(1L)).thenReturn(Optional.of(tracking));
		when(deliveryTrackingRepository.save(any(DeliveryTracking.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DeliveryTrackingResponse response = deliveryService.updateDeliveryStatus(1L, DeliveryStatus.PICKED_UP);

		assertNotNull(response);
		assertEquals(DeliveryStatus.PICKED_UP, response.getDeliveryStatus());
		assertEquals(35, response.getEtaMinutes());

		verify(deliveryTrackingRepository, times(1)).save(any(DeliveryTracking.class));
	}

	@Test
	void updateDeliveryStatusWhenOnTheWayShouldUpdateEta() {

		tracking.setDeliveryPartnerId(1L);

		when(deliveryTrackingRepository.findByOrderId(1L)).thenReturn(Optional.of(tracking));
		when(deliveryTrackingRepository.save(any(DeliveryTracking.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DeliveryTrackingResponse response = deliveryService.updateDeliveryStatus(1L, DeliveryStatus.ON_THE_WAY);

		assertNotNull(response);
		assertEquals(DeliveryStatus.ON_THE_WAY, response.getDeliveryStatus());
		assertEquals(25, response.getEtaMinutes());

		verify(deliveryTrackingRepository, times(1)).save(any(DeliveryTracking.class));
	}

	@Test
	void updateDeliveryStatusWhenNearYouShouldUpdateEtaToFive() {

		tracking.setDeliveryPartnerId(1L);

		when(deliveryTrackingRepository.findByOrderId(1L)).thenReturn(Optional.of(tracking));
		when(deliveryTrackingRepository.save(any(DeliveryTracking.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DeliveryTrackingResponse response = deliveryService.updateDeliveryStatus(1L, DeliveryStatus.NEAR_YOU);

		assertNotNull(response);
		assertEquals(DeliveryStatus.NEAR_YOU, response.getDeliveryStatus());
		assertEquals(5, response.getEtaMinutes());

		verify(deliveryTrackingRepository, times(1)).save(any(DeliveryTracking.class));
	}

	@Test
	void updateDeliveryStatusWhenDeliveredShouldMarkPartnerAvailable() {

		tracking.setDeliveryPartnerId(1L);
		tracking.setDeliveryPartnerName("Rahul Kumar");
		tracking.setDeliveryPartnerMobile("9876501234");

		partner.setAvailable(false);

		when(deliveryTrackingRepository.findByOrderId(1L)).thenReturn(Optional.of(tracking));
		when(deliveryPartnerRepository.findById(1L)).thenReturn(Optional.of(partner));
		when(deliveryPartnerRepository.save(any(DeliveryPartner.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));
		when(deliveryTrackingRepository.save(any(DeliveryTracking.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		DeliveryTrackingResponse response = deliveryService.updateDeliveryStatus(1L, DeliveryStatus.DELIVERED);

		assertNotNull(response);
		assertEquals(DeliveryStatus.DELIVERED, response.getDeliveryStatus());
		assertEquals(0, response.getEtaMinutes());
		assertTrue(partner.getAvailable());

		verify(deliveryPartnerRepository, times(1)).findById(1L);
		verify(deliveryPartnerRepository, times(1)).save(partner);
		verify(deliveryTrackingRepository, times(1)).save(any(DeliveryTracking.class));
	}
}