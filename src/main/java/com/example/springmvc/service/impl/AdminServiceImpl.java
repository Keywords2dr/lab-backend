package com.example.springmvc.service.impl;

import com.example.springmvc.dto.DashboardStats;
import com.example.springmvc.repository.*;
import com.example.springmvc.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RentTicketRepository rentTicketRepository;
    private final ChemicalRepository chemicalRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    @Override
    public DashboardStats getDashboardStats() {
        DashboardStats stats = new DashboardStats();

        // 1. Đếm số phiếu đang chờ duyệt (PENDING)
        long pendingCount = rentTicketRepository.findByStatusOrderByCreatedDateDesc("PENDING").size();
        stats.setPendingTicketsCount(pendingCount);

        // 2. Đếm số hóa chất sắp hết hàng (nhỏ hơn 10 đơn vị)
        long lowStockCount = chemicalRepository.findLowStockChemicals(new BigDecimal("10")).size();
        stats.setLowStockChemicalsCount(lowStockCount);

        // 3. Đếm tổng số tài sản cố định
        stats.setTotalAssetsCount(assetRepository.count());

        // 4. Đếm số người dùng đang hoạt động (Active = true)
        long activeUsers = userRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getIsActive()))
                .count();
        stats.setActiveUsersCount(activeUsers);

        return stats;
    }
}