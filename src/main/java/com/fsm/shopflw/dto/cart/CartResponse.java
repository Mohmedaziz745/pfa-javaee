package com.fsm.shopflw.dto.cart;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CartResponse(
        Long id,
        List<CartItemResponse> lignes,
        String couponCode,
        BigDecimal sousTotal,
        BigDecimal remise,
        BigDecimal fraisLivraison,
        BigDecimal totalTtc,
        LocalDateTime dateModification
) {
}
