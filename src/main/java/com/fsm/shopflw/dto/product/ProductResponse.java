package com.fsm.shopflw.dto.product;

import com.fsm.shopflw.dto.common.SellerSummaryDto;
import com.fsm.shopflw.dto.review.ReviewResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
        Long id,
        String nom,
        String description,
        BigDecimal prix,
        BigDecimal prixPromo,
        Integer stock,
        boolean actif,
        Long salesCount,
        Double averageRating,
        Integer reviewCount,
        LocalDateTime dateCreation,
        List<String> categories,
        List<String> images,
        SellerSummaryDto seller,
        List<ProductVariantResponse> variants,
        List<ReviewResponse> reviews
) {
}
