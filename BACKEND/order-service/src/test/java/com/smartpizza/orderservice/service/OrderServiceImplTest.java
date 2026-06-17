package com.smartpizza.orderservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.smartpizza.orderservice.dto.OrderResponse;
import com.smartpizza.orderservice.dto.PlaceOrderRequest;
import com.smartpizza.orderservice.entity.CartItem;
import com.smartpizza.orderservice.entity.Coupon;
import com.smartpizza.orderservice.entity.CustomerOrder;
import com.smartpizza.orderservice.entity.DeliveryPartner;
import com.smartpizza.orderservice.entity.DeliveryTracking;
import com.smartpizza.orderservice.entity.OrderItem;
import com.smartpizza.orderservice.enums.OrderStatus;
import com.smartpizza.orderservice.exception.ResourceNotFoundException;
import com.smartpizza.orderservice.repository.CartItemRepository;
import com.smartpizza.orderservice.repository.CouponRepository;
import com.smartpizza.orderservice.repository.CustomerOrderRepository;
import com.smartpizza.orderservice.repository.DeliveryPartnerRepository;
import com.smartpizza.orderservice.repository.DeliveryTrackingRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

	@Mock
	private CartItemRepository cartItemRepository;

	@Mock
	private CustomerOrderRepository customerOrderRepository;

	@Mock
	private CouponRepository couponRepository;

	@Mock
	private DeliveryTrackingRepository deliveryTrackingRepository;

	@Mock
	private DeliveryPartnerRepository deliveryPartnerRepository;

	@InjectMocks
	private OrderServiceImpl orderService;

	private CartItem cartItem;
	private PlaceOrderRequest placeOrderRequest;
	private CustomerOrder savedOrder;
	private Coupon coupon;
	private DeliveryPartner deliveryPartner;

	@BeforeEach
	void setup() {

		cartItem = new CartItem();
		cartItem.setCartItemId(1L);
		cartItem.setUserId(1L);
		cartItem.setPizzaId(1L);
		cartItem.setPizzaName("Farmhouse Pizza");
		cartItem.setPrice(349.0);
		cartItem.setQuantity(2);
		cartItem.setTotalPrice(698.0);

		placeOrderRequest = new PlaceOrderRequest();
		placeOrderRequest.setCouponCode("");
		placeOrderRequest.setDeliveryAddress("Electronic City Phase 1");
		placeOrderRequest.setCustomerMobile("9876543210");

		savedOrder = new CustomerOrder();
		savedOrder.setOrderId(1L);
		savedOrder.setUserId(1L);
		savedOrder.setSubtotal(698.0);
		savedOrder.setDiscountAmount(0.0);
		savedOrder.setTotalAmount(698.0);
		savedOrder.setCouponCode("");
		savedOrder.setDeliveryAddress("Electronic City Phase 1");
		savedOrder.setDeliveryCity("Bengaluru");
		savedOrder.setCustomerMobile("9876543210");
		savedOrder.setOrderStatus(OrderStatus.ASSIGNED);

		coupon = new Coupon();
		coupon.setCouponId(1L);
		coupon.setCouponCode("SAVE10");
		coupon.setDescription("10 percent discount");
		coupon.setDiscountPercentage(10.0);
		coupon.setMinimumOrderAmount(300.0);
		coupon.setActive(true);

		deliveryPartner = new DeliveryPartner();
		deliveryPartner.setPartnerId(1L);
		deliveryPartner.setAuthUserId(3L);
		deliveryPartner.setPartnerName("Rahul Kumar");
		deliveryPartner.setMobileNumber("9876501234");
		deliveryPartner.setEmail("rahul.delivery@gmail.com");
		deliveryPartner.setCity("Bengaluru");
		deliveryPartner.setAvailable(true);
	}

	@Test
	void placeOrderWithoutCouponShouldPlaceOrderSuccessfullyAndAutoAssignDeliveryPartner() {

		when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

		when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
			CustomerOrder order = invocation.getArgument(0);
			order.setOrderId(1L);
			return order;
		});

		when(deliveryPartnerRepository.findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru"))
				.thenReturn(Optional.of(deliveryPartner));

		when(deliveryPartnerRepository.save(any(DeliveryPartner.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		when(deliveryTrackingRepository.save(any(DeliveryTracking.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		OrderResponse response = orderService.placeOrder(1L, placeOrderRequest);

		assertNotNull(response);
		assertEquals(1L, response.getOrderId());
		assertEquals(1L, response.getUserId());
		assertEquals(698.0, response.getSubtotal());
		assertEquals(0.0, response.getDiscountAmount());
		assertEquals(698.0, response.getTotalAmount());
		assertEquals("Electronic City Phase 1", response.getDeliveryAddress());
		assertEquals("Bengaluru", response.getDeliveryCity());
		assertEquals(OrderStatus.ASSIGNED, response.getOrderStatus());
		assertEquals(1, response.getOrderItems().size());
		assertEquals("Farmhouse Pizza", response.getOrderItems().get(0).getPizzaName());

		verify(cartItemRepository, times(1)).findByUserId(1L);
		verify(customerOrderRepository, times(2)).save(any(CustomerOrder.class));
		verify(deliveryPartnerRepository, times(1)).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
		verify(deliveryPartnerRepository, times(1)).save(any(DeliveryPartner.class));
		verify(deliveryTrackingRepository, times(1)).save(any(DeliveryTracking.class));
		verify(cartItemRepository, times(1)).deleteByUserId(1L);
	}

	@Test
	void placeOrderWithValidCouponShouldApplyDiscountAndAutoAssignDeliveryPartner() {

		placeOrderRequest.setCouponCode("SAVE10");

		when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
		when(couponRepository.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

		when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
			CustomerOrder order = invocation.getArgument(0);
			order.setOrderId(1L);
			return order;
		});

		when(deliveryPartnerRepository.findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru"))
				.thenReturn(Optional.of(deliveryPartner));

		when(deliveryPartnerRepository.save(any(DeliveryPartner.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		when(deliveryTrackingRepository.save(any(DeliveryTracking.class)))
				.thenAnswer(invocation -> invocation.getArgument(0));

		OrderResponse response = orderService.placeOrder(1L, placeOrderRequest);

		assertNotNull(response);
		assertEquals(698.0, response.getSubtotal());
		assertEquals(69.8, response.getDiscountAmount());
		assertEquals(628.2, response.getTotalAmount());
		assertEquals("SAVE10", response.getCouponCode());
		assertEquals(OrderStatus.ASSIGNED, response.getOrderStatus());

		verify(couponRepository, times(1)).findByCouponCode("SAVE10");
		verify(customerOrderRepository, times(2)).save(any(CustomerOrder.class));
		verify(deliveryPartnerRepository, times(1)).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
		verify(deliveryPartnerRepository, times(1)).save(any(DeliveryPartner.class));
		verify(deliveryTrackingRepository, times(1)).save(any(DeliveryTracking.class));
		verify(cartItemRepository, times(1)).deleteByUserId(1L);
	}

	@Test
	void placeOrderWhenNoDeliveryPartnerAvailableShouldThrowException() {

		when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));

		when(customerOrderRepository.save(any(CustomerOrder.class))).thenAnswer(invocation -> {
			CustomerOrder order = invocation.getArgument(0);
			order.setOrderId(1L);
			return order;
		});

		when(deliveryPartnerRepository.findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru"))
				.thenReturn(Optional.empty());

		assertThrows(RuntimeException.class, () -> {
			orderService.placeOrder(1L, placeOrderRequest);
		});

		verify(cartItemRepository, times(1)).findByUserId(1L);
		verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
		verify(deliveryPartnerRepository, times(1)).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
		verify(deliveryTrackingRepository, never()).save(any(DeliveryTracking.class));
		verify(cartItemRepository, never()).deleteByUserId(1L);
	}

	@Test
	void placeOrderWhenCartIsEmptyShouldThrowException() {

		when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

		assertThrows(RuntimeException.class, () -> {
			orderService.placeOrder(1L, placeOrderRequest);
		});

		verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
		verify(deliveryPartnerRepository, never()).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
		verify(deliveryTrackingRepository, never()).save(any(DeliveryTracking.class));
	}

	@Test
	void placeOrderWhenCouponNotFoundShouldThrowException() {

		placeOrderRequest.setCouponCode("WRONG");

		when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
		when(couponRepository.findByCouponCode("WRONG")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.placeOrder(1L, placeOrderRequest);
		});

		verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
		verify(deliveryPartnerRepository, never()).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
	}

	@Test
	void placeOrderWhenCouponInactiveShouldThrowException() {

		placeOrderRequest.setCouponCode("SAVE10");
		coupon.setActive(false);

		when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
		when(couponRepository.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

		assertThrows(RuntimeException.class, () -> {
			orderService.placeOrder(1L, placeOrderRequest);
		});

		verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
		verify(deliveryPartnerRepository, never()).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
	}

	@Test
	void placeOrderWhenMinimumAmountNotMetShouldThrowException() {

		placeOrderRequest.setCouponCode("SAVE10");
		coupon.setMinimumOrderAmount(1000.0);

		when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
		when(couponRepository.findByCouponCode("SAVE10")).thenReturn(Optional.of(coupon));

		assertThrows(RuntimeException.class, () -> {
			orderService.placeOrder(1L, placeOrderRequest);
		});

		verify(customerOrderRepository, never()).save(any(CustomerOrder.class));
		verify(deliveryPartnerRepository, never()).findFirstByCityIgnoreCaseAndAvailableTrue("Bengaluru");
	}

	@Test
	void getOrdersByUserShouldReturnUserOrders() {

		when(customerOrderRepository.findByUserId(1L)).thenReturn(List.of(savedOrder));

		List<OrderResponse> responses = orderService.getOrdersByUser(1L);

		assertNotNull(responses);
		assertEquals(1, responses.size());
		assertEquals(1L, responses.get(0).getOrderId());
		assertEquals(1L, responses.get(0).getUserId());
		assertEquals(OrderStatus.ASSIGNED, responses.get(0).getOrderStatus());

		verify(customerOrderRepository, times(1)).findByUserId(1L);
	}

	@Test
	void getOrderByIdWhenOrderExistsShouldReturnOrder() {

		when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

		OrderResponse response = orderService.getOrderById(1L);

		assertNotNull(response);
		assertEquals(1L, response.getOrderId());
		assertEquals(698.0, response.getTotalAmount());
		assertEquals("Bengaluru", response.getDeliveryCity());

		verify(customerOrderRepository, times(1)).findById(1L);
	}

	@Test
	void getOrderByIdWhenOrderNotFoundShouldThrowException() {

		when(customerOrderRepository.findById(99L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> {
			orderService.getOrderById(99L);
		});

		verify(customerOrderRepository, times(1)).findById(99L);
	}

	@Test
	void updateOrderStatusShouldUpdateStatus() {

		CustomerOrder updatedOrder = new CustomerOrder();
		updatedOrder.setOrderId(1L);
		updatedOrder.setUserId(1L);
		updatedOrder.setSubtotal(698.0);
		updatedOrder.setDiscountAmount(0.0);
		updatedOrder.setTotalAmount(698.0);
		updatedOrder.setDeliveryAddress("Electronic City Phase 1");
		updatedOrder.setDeliveryCity("Bengaluru");
		updatedOrder.setCustomerMobile("9876543210");
		updatedOrder.setOrderStatus(OrderStatus.CONFIRMED);

		when(customerOrderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));
		when(customerOrderRepository.save(any(CustomerOrder.class))).thenReturn(updatedOrder);

		OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

		assertNotNull(response);
		assertEquals(OrderStatus.CONFIRMED, response.getOrderStatus());

		verify(customerOrderRepository, times(1)).findById(1L);
		verify(customerOrderRepository, times(1)).save(any(CustomerOrder.class));
	}

	@Test
	void getAllOrdersShouldReturnAllOrders() {

		when(customerOrderRepository.findAll()).thenReturn(List.of(savedOrder));

		List<OrderResponse> responses = orderService.getAllOrders();

		assertNotNull(responses);
		assertEquals(1, responses.size());
		assertEquals(1L, responses.get(0).getOrderId());

		verify(customerOrderRepository, times(1)).findAll();
	}

	@Test
	void getTopOrderedPizzaIdsByUserShouldReturnMostOrderedPizzaIds() {

		OrderItem item1 = new OrderItem();
		item1.setPizzaId(1L);
		item1.setQuantity(2);

		OrderItem item2 = new OrderItem();
		item2.setPizzaId(2L);
		item2.setQuantity(5);

		OrderItem item3 = new OrderItem();
		item3.setPizzaId(1L);
		item3.setQuantity(1);

		CustomerOrder order1 = new CustomerOrder();
		order1.setOrderId(1L);
		order1.setUserId(1L);
		order1.setOrderItems(List.of(item1, item2));

		CustomerOrder order2 = new CustomerOrder();
		order2.setOrderId(2L);
		order2.setUserId(1L);
		order2.setOrderItems(List.of(item3));

		when(customerOrderRepository.findByUserIdOrderByOrderDateDesc(1L)).thenReturn(List.of(order1, order2));

		List<Long> topPizzaIds = orderService.getTopOrderedPizzaIdsByUser(1L);

		assertNotNull(topPizzaIds);
		assertEquals(2, topPizzaIds.size());
		assertEquals(2L, topPizzaIds.get(0));
		assertEquals(1L, topPizzaIds.get(1));

		verify(customerOrderRepository, times(1)).findByUserIdOrderByOrderDateDesc(1L);
	}

	@Test
	void getTopOrderedPizzaIdsByUserWhenNoOrdersShouldReturnEmptyList() {

		when(customerOrderRepository.findByUserIdOrderByOrderDateDesc(1L)).thenReturn(List.of());

		List<Long> topPizzaIds = orderService.getTopOrderedPizzaIdsByUser(1L);

		assertNotNull(topPizzaIds);
		assertTrue(topPizzaIds.isEmpty());

		verify(customerOrderRepository, times(1)).findByUserIdOrderByOrderDateDesc(1L);
	}
}