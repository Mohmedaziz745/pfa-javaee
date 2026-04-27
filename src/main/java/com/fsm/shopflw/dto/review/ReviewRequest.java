package com.fsm.shopflw.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull Long productId,
        @NotNull @Min(1) @Max(5) Integer note,
        @NotBlank String commentaire
) {
}
