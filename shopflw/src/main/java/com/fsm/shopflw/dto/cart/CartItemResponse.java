package com.fsm.shopflw.dto.cart;

import java.math.BigDecimal;

public record CartItemResponse(
        Long itemId,
        Long productId,
        Long variantId,
        String productName,
        String variantLabel,
        Integer quantite,
        Integer stockAvailable,
        BigDecimal prixUnitaire,
        BigDecimal totalLigne
) {
}
