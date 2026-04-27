package com.fsm.shopflw.dto.coupon;

import com.fsm.shopflw.model.enums.CouponType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponRequest(
        @NotBlank String code,
        @NotNull CouponType type,
        @NotNull @DecimalMin("0.0") BigDecimal valeur,
        LocalDateTime dateExpiration,
        @NotNull Integer usagesMax,
        boolean actif
) {
}
