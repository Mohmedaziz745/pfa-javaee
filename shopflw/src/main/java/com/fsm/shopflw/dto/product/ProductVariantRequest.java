package com.fsm.shopflw.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductVariantRequest(
        @NotBlank String attribut,
        @NotBlank String valeur,
        @NotNull @Min(0) Integer stockSupplementaire,
        BigDecimal prixDelta
) {
}
