package com.example.springmvc.dto;

import lombok.Data;

@Data
public class DashboardStats {
    private Long pendingTicketsCount;
    private Long lowStockChemicalsCount;
    private Long totalAssetsCount;
    private Long activeUsersCount;
}