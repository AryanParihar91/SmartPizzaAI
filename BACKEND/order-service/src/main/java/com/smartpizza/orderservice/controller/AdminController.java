package com.smartpizza.orderservice.controller;

import com.smartpizza.orderservice.dto.AdminAnalyticsResponse;
import com.smartpizza.orderservice.dto.AdminDashboardResponse;
import com.smartpizza.orderservice.dto.OrderResponse;
import com.smartpizza.orderservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // admin dashboard api
    @GetMapping("/dashboard")
    public AdminDashboardResponse getDashboard() {
        return adminService.getDashboard();
    }

    // get all orders
    @GetMapping("/orders")
    public List<OrderResponse> getAllOrders() {
        return adminService.getAllOrders();
    }

    // admin analytics api
    @GetMapping("/analytics")
    public AdminAnalyticsResponse getAnalytics() {
        return adminService.getAnalytics();
    }
}