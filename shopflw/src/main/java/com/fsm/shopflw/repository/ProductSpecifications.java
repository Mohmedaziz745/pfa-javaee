package com.fsm.shopflw.repository;

import com.fsm.shopflw.model.Product;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> isActive() {
        return (root, query, cb) -> cb.isTrue(root.get("actif"));
    }

    private static final Specification<Product> NO_OP = (root, query, cb) -> null;

    public static Specification<Product> hasCategory(Long categoryId) {
        return categoryId == null ? NO_OP : (root, query, cb) -> cb.equal(root.join("categories").get("id"), categoryId);
    }

    public static Specification<Product> hasSeller(Long sellerId) {
        return sellerId == null ? NO_OP : (root, query, cb) -> cb.equal(root.get("seller").get("id"), sellerId);
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return minPrice == null ? NO_OP : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("prix"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return maxPrice == null ? NO_OP : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("prix"), maxPrice);
    }

    public static Specification<Product> promo(Boolean promo) {
        return Boolean.TRUE.equals(promo) ? (root, query, cb) -> cb.isNotNull(root.get("prixPromo")) : NO_OP;
    }
}
