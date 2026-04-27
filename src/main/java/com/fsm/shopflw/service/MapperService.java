package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.cart.CartItemResponse;
import com.fsm.shopflw.dto.common.AddressDto;
import com.fsm.shopflw.dto.common.CategoryTreeResponse;
import com.fsm.shopflw.dto.common.SellerSummaryDto;
import com.fsm.shopflw.dto.coupon.CouponResponse;
import com.fsm.shopflw.dto.order.OrderItemResponse;
import com.fsm.shopflw.dto.order.OrderResponse;
import com.fsm.shopflw.dto.product.ProductResponse;
import com.fsm.shopflw.dto.product.ProductVariantResponse;
import com.fsm.shopflw.dto.review.ReviewResponse;
import com.fsm.shopflw.model.Address;
import com.fsm.shopflw.model.CartItem;
import com.fsm.shopflw.model.Category;
import com.fsm.shopflw.model.Coupon;
import com.fsm.shopflw.model.OrderEntity;
import com.fsm.shopflw.model.OrderItem;
import com.fsm.shopflw.model.Product;
import com.fsm.shopflw.model.ProductVariant;
import com.fsm.shopflw.model.Review;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MapperService {

    public AddressDto toAddressDto(Address address) {
        return new AddressDto(address.getId(), address.getRue(), address.getVille(), address.getCodePostal(), address.getPays(), address.isPrincipal());
    }

    public SellerSummaryDto toSellerSummary(Product product) {
        if (product.getSeller().getSellerProfile() == null) {
            return new SellerSummaryDto(product.getSeller().getId(), product.getSeller().getEmail(), null, null, 0.0);
        }
        return new SellerSummaryDto(
                product.getSeller().getId(),
                product.getSeller().getSellerProfile().getNomBoutique(),
                product.getSeller().getSellerProfile().getDescription(),
                product.getSeller().getSellerProfile().getLogo(),
                product.getSeller().getSellerProfile().getNote()
        );
    }

    public ProductVariantResponse toVariantResponse(ProductVariant variant) {
        return new ProductVariantResponse(
                variant.getId(),
                variant.getAttribut(),
                variant.getValeur(),
                variant.getStockSupplementaire(),
                variant.getPrixDelta()
        );
    }

    public ReviewResponse toReviewResponse(Review review) {
        return new ReviewResponse(
                review.getId(),
                review.getCustomer().getId(),
                review.getCustomer().getPrenom() + " " + review.getCustomer().getNom(),
                review.getNote(),
                review.getCommentaire(),
                review.isApprouve(),
                review.getDateCreation()
        );
    }

    public ProductResponse toProductResponse(Product product, Double averageRating, List<Review> reviews) {
        List<String> images = List.copyOf(product.getImages());

        return new ProductResponse(
                product.getId(),
                product.getNom(),
                product.getDescription(),
                product.getPrix(),
                product.getPrixPromo(),
                product.getStock(),
                product.isActif(),
                product.getSalesCount(),
                averageRating,
                reviews.size(),
                product.getDateCreation(),
                product.getCategories().stream().map(Category::getNom).sorted().toList(),
                images,
                toSellerSummary(product),
                product.getVariants().stream().map(this::toVariantResponse).toList(),
                reviews.stream().map(this::toReviewResponse).toList()
        );
    }

    public CartItemResponse toCartItemResponse(CartItem item) {
        BigDecimal unitPrice = effectivePrice(item.getProduct(), item.getVariant());
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getVariant() != null ? item.getVariant().getId() : null,
                item.getProduct().getNom(),
                item.getVariant() != null ? item.getVariant().getAttribut() + ": " + item.getVariant().getValeur() : null,
                item.getQuantite(),
                unitPrice,
                unitPrice.multiply(BigDecimal.valueOf(item.getQuantite()))
        );
    }

    public OrderResponse toOrderResponse(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getNumeroCommande(),
                order.getStatut(),
                order.getSousTotal(),
                order.getFraisLivraison(),
                order.getTotalTTC(),
                order.isNew(),
                order.getDateCommande(),
                toAddressDto(order.getAdresseLivraison()),
                order.getLignes().stream().map(this::toOrderItemResponse).toList()
        );
    }

    public OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getVariant() != null ? item.getVariant().getId() : null,
                item.getProduct().getNom(),
                item.getVariant() != null ? item.getVariant().getAttribut() + ": " + item.getVariant().getValeur() : null,
                item.getQuantite(),
                item.getPrixUnitaire()
        );
    }

    public CouponResponse toCouponResponse(Coupon coupon, boolean valid) {
        return new CouponResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getType(),
                coupon.getValeur(),
                coupon.getDateExpiration(),
                coupon.getUsagesMax(),
                coupon.getUsagesActuels(),
                coupon.isActif(),
                valid
        );
    }

    public CategoryTreeResponse toCategoryTreeResponse(Category category) {
        return new CategoryTreeResponse(
                category.getId(),
                category.getNom(),
                category.getDescription(),
                category.getChildren().stream().map(this::toCategoryTreeResponse).toList()
        );
    }

    public BigDecimal effectivePrice(Product product, ProductVariant variant) {
        BigDecimal base = product.getPrixPromo() != null ? product.getPrixPromo() : product.getPrix();
        return variant == null ? base : base.add(variant.getPrixDelta());
    }
}
