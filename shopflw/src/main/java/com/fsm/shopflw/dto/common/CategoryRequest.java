package com.fsm.shopflw.dto.common;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String nom,
        String description,
        Long parentId
) {
}
