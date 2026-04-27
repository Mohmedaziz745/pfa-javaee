package com.fsm.shopflw.dto.product;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record ProductRequest(
        @NotBlank String nom,
        @NotBlank String description,
        @NotNull @DecimalMin("0.0") BigDecimal prix,
        BigDecimal prixPromo,
        @NotNull @Min(0) Integer stock,
        @NotEmpty Set<Long> categoryIds,
        List<String> images,
        @Valid List<ProductVariantRequest> variants
) {
}
