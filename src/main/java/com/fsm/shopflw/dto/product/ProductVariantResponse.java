package com.fsm.shopflw.dto.product;

import java.math.BigDecimal;

public record ProductVariantResponse(
        Long id,
        String attribut,
        String valeur,
        Integer stockSupplementaire,
        BigDecimal prixDelta
) {
}
