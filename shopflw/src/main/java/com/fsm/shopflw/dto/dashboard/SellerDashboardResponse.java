package com.fsm.shopflw.dto.dashboard;

import java.math.BigDecimal;
import java.util.List;

public record SellerDashboardResponse(
        BigDecimal revenus,
        long commandesRecues,
        long commandesEnAttente,
        List<String> alertesStockFaible
) {
}
