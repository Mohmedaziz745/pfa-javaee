package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.cart.CartItemRequest;
import com.fsm.shopflw.dto.cart.CartItemUpdateRequest;
import com.fsm.shopflw.dto.cart.CartResponse;
import com.fsm.shopflw.exception.BadRequestException;
import com.fsm.shopflw.exception.NotFoundException;
import com.fsm.shopflw.model.Cart;
import com.fsm.shopflw.model.CartItem;
import com.fsm.shopflw.model.Coupon;
import com.fsm.shopflw.model.Product;
import com.fsm.shopflw.model.ProductVariant;
import com.fsm.shopflw.model.enums.CouponType;
import com.fsm.shopflw.repository.CartItemRepository;
import com.fsm.shopflw.repository.CartRepository;
import com.fsm.shopflw.repository.ProductRepository;
import com.fsm.shopflw.repository.ProductVariantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CartService {

    private static final BigDecimal SHIPPING_FEE = BigDecimal.valueOf(15);

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final SecurityFacade securityFacade;
    private final MapperService mapperService;
    private final CouponService couponService;

    @Transactional(readOnly = true)
    public CartResponse getCurrentCart() {
        return toResponse(getOrCreateCart());
    }

    @Transactional
    public CartResponse addItem(CartItemRequest request) {
        Cart cart = getOrCreateCart();
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new NotFoundException("Produit introuvable"));
        ProductVariant variant = request.variantId() == null ? null : variantRepository.findById(request.variantId())
                .orElseThrow(() -> new NotFoundException("Variante introuvable"));
        validateStock(product, variant, request.quantite());

        CartItem existing = cart.getLignes().stream()
                .filter(item -> item.getProduct().getId().equals(request.productId())
                        && ((item.getVariant() == null && request.variantId() == null)
                        || (item.getVariant() != null && item.getVariant().getId().equals(request.variantId()))))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            int newQuantity = existing.getQuantite() + request.quantite();
            validateStock(product, variant, newQuantity);
            existing.setQuantite(newQuantity);
        } else {
            cart.getLignes().add(CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .variant(variant)
                    .quantite(request.quantite())
                    .build());
        }
        cart.setDateModification(LocalDateTime.now());
        return toResponse(cartRepository.saveAndFlush(cart));
    }

    @Transactional
    public CartResponse updateItem(Long itemId, CartItemUpdateRequest request) {
        Cart cart = getOrCreateCart();
        CartItem item = cart.getLignes().stream().filter(value -> value.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Article de panier introuvable"));
        validateStock(item.getProduct(), item.getVariant(), request.quantite());
        item.setQuantite(request.quantite());
        cart.setDateModification(LocalDateTime.now());
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(Long itemId) {
        Cart cart = getOrCreateCart();
        CartItem item = cart.getLignes().stream().filter(value -> value.getId().equals(itemId)).findFirst()
                .orElseThrow(() -> new NotFoundException("Article de panier introuvable"));
        cart.getLignes().remove(item);
        cartItemRepository.delete(item);
        cart.setDateModification(LocalDateTime.now());
        return toResponse(cart);
    }

    @Transactional
    public CartResponse applyCoupon(String code) {
        Cart cart = getOrCreateCart();
        cart.setCoupon(couponService.requireValidCoupon(code));
        cart.setDateModification(LocalDateTime.now());
        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeCoupon() {
        Cart cart = getOrCreateCart();
        cart.setCoupon(null);
        cart.setDateModification(LocalDateTime.now());
        return toResponse(cart);
    }

    public Cart getOrCreateCart() {
        return cartRepository.findByCustomerId(securityFacade.currentUser().getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .customer(securityFacade.currentUser())
                        .dateModification(LocalDateTime.now())
                        .build()));
    }

    public CartResponse toResponse(Cart cart) {
        BigDecimal subTotal = cart.getLignes().stream()
                .map(item -> mapperService.toCartItemResponse(item).totalLigne())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remise = computeDiscount(subTotal, cart.getCoupon());
        BigDecimal fraisLivraison = cart.getLignes().isEmpty() ? BigDecimal.ZERO : SHIPPING_FEE;
        BigDecimal total = subTotal.subtract(remise).add(fraisLivraison).max(BigDecimal.ZERO);
        return new CartResponse(
                cart.getId(),
                cart.getLignes().stream().map(mapperService::toCartItemResponse).toList(),
                cart.getCoupon() != null ? cart.getCoupon().getCode() : null,
                subTotal,
                remise,
                fraisLivraison,
                total,
                cart.getDateModification()
        );
    }

    public BigDecimal computeDiscount(BigDecimal subTotal, Coupon coupon) {
        if (coupon == null || !couponService.isValid(coupon)) {
            return BigDecimal.ZERO;
        }
        if (coupon.getType() == CouponType.PERCENT) {
            return subTotal.multiply(coupon.getValeur()).divide(BigDecimal.valueOf(100));
        }
        return coupon.getValeur().min(subTotal);
    }

    private void validateStock(Product product, ProductVariant variant, Integer quantity) {
        int available = product.getStock() + (variant != null ? variant.getStockSupplementaire() : 0);
        if (quantity > available) {
            throw new BadRequestException("Stock insuffisant");
        }
    }
}
