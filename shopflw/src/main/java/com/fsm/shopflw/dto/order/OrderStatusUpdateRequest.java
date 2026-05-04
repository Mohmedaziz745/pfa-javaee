package com.fsm.shopflw.dto.order;

import com.fsm.shopflw.model.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(@NotNull OrderStatus statut) {
}
