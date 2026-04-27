package com.fsm.shopflw.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemUpdateRequest(@NotNull @Min(1) Integer quantite) {
}
