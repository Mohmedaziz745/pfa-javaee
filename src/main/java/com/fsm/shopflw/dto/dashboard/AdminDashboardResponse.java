package com.fsm.shopflw.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record AdminDashboardResponse(
        BigDecimal chiffreAffaires,
        long totalOrders,
        long pendingOrders,
        List<String> topProducts,
        List<String> topSellers,
        List<String> recentOrders
) {
}
