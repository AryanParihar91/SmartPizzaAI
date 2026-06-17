package com.smartpizza.orderservice.service;

import java.util.List;

import com.smartpizza.orderservice.dto.AdminAnalyticsResponse;
import com.smartpizza.orderservice.dto.AdminDashboardResponse;
import com.smartpizza.orderservice.dto.OrderResponse;

public interface AdminService {

	AdminDashboardResponse getDashboard();

	List<OrderResponse> getAllOrders();

	AdminAnalyticsResponse getAnalytics();

}