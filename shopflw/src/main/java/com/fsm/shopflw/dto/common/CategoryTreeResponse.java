package com.fsm.shopflw.dto.common;

import java.util.List;

public record CategoryTreeResponse(
        Long id,
        String nom,
        String description,
        List<CategoryTreeResponse> children
) {
}
