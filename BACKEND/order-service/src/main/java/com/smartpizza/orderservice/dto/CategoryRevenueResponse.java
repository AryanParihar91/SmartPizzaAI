package com.smartpizza.orderservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRevenueResponse {

    private String categoryName;

    // Revenue includes GST because admin should see paid value
    private Double revenue;
}