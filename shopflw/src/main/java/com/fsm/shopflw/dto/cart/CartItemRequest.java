package com.fsm.shopflw.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull Long productId,
        Long variantId,
        @NotNull @Min(1) Integer quantite
) {
}
