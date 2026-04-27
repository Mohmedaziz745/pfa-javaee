package com.fsm.shopflw.dto.order;

import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(@NotNull Long addressId) {
}
