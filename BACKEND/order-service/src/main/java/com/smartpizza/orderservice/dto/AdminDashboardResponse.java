package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDashboardResponse {

    private Long totalOrders;
    private Double totalRevenue;
    private Long deliveredOrders;
    private Long cancelledOrders;
    private Long activeOrders;
}
