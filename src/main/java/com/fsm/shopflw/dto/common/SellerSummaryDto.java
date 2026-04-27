package com.fsm.shopflw.dto.common;

public record SellerSummaryDto(
        Long id,
        String nomBoutique,
        String description,
        String logo,
        Double note
) {
}
