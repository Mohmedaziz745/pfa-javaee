package com.fsm.shopflw.dto.review;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long id,
        Long customerId,
        String customerName,
        Integer note,
        String commentaire,
        boolean approuve,
        LocalDateTime dateCreation
) {
}
