package com.fsm.shopflw.model;

import com.fsm.shopflw.model.enums.CouponType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Coupon extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal valeur;

    private LocalDateTime dateExpiration;

    @Column(nullable = false)
    private Integer usagesMax;

    @Builder.Default
    @Column(nullable = false)
    private Integer usagesActuels = 0;

    @Column(nullable = false)
    private boolean actif;
}
