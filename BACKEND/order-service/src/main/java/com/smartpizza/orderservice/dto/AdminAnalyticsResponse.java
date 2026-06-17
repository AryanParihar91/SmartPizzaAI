package com.smartpizza.orderservice.dto;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAnalyticsResponse {

	private Integer totalItemsSold;

	private Double baseRevenue;
	private Double gstCollected;
	private Double finalRevenueWithGst;
	private Double averageOrderValue;

	private List<TopSellingPizzaResponse> topSellingPizzas;
	private List<CategoryRevenueResponse> categoryRevenue;

	private Map<String, Long> orderStatusCounts;

	private Long activeDeliveryCount;
}