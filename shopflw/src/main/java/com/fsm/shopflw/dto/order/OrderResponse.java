package com.fsm.shopflw.dto.order;

import com.fsm.shopflw.dto.common.AddressDto;
import com.fsm.shopflw.model.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String numeroCommande,
        OrderStatus statut,
        BigDecimal sousTotal,
        BigDecimal fraisLivraison,
        BigDecimal totalTTC,
        boolean isNew,
        LocalDateTime dateCommande,
        AddressDto adresseLivraison,
        List<OrderItemResponse> lignes
) {
}
