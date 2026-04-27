package com.fsm.shopflw.model;

import com.fsm.shopflw.model.enums.OrderStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus statut;

    @Column(nullable = false, unique = true)
    private String numeroCommande;

    @ManyToOne(optional = false)
    @JoinColumn(name = "adresse_livraison_id", nullable = false)
    private Address adresseLivraison;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal sousTotal;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal fraisLivraison;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalTTC;

    @Column(nullable = false)
    private LocalDateTime dateCommande;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> lignes = new ArrayList<>();

    @Builder.Default
    @Column(nullable = false)
    private boolean isNew = true;
}
