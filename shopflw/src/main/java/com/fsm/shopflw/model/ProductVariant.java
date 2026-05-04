package com.fsm.shopflw.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ProductVariant extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String attribut;

    @Column(nullable = false)
    private String valeur;

    @Column(nullable = false)
    private Integer stockSupplementaire;

    @Builder.Default
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal prixDelta = BigDecimal.ZERO;
}
