package com.fsm.shopflw.dto.order;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long productId,
        Long variantId,
        String productName,
        String variantLabel,
        Integer quantite,
        BigDecimal prixUnitaire
) {
}
