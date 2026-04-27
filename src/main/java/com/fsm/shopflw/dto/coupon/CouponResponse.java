package com.fsm.shopflw.dto.coupon;

import com.fsm.shopflw.model.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CouponResponse(
        Long id,
        String code,
        CouponType type,
        BigDecimal valeur,
        LocalDateTime dateExpiration,
        Integer usagesMax,
        Integer usagesActuels,
        boolean actif,
        boolean valid
) {
}
