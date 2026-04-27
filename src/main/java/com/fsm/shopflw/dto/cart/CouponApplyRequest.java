package com.fsm.shopflw.dto.cart;

import jakarta.validation.constraints.NotBlank;

public record CouponApplyRequest(@NotBlank String code) {
}
