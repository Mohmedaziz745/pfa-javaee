package com.fsm.shopflw.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cart extends BaseEntity {

    @OneToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private User customer;

    @Builder.Default
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> lignes = new ArrayList<>();

    private LocalDateTime dateModification;

    @OneToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;
}
