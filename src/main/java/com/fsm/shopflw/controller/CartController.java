package com.fsm.shopflw.controller;

import com.fsm.shopflw.dto.cart.CartItemRequest;
import com.fsm.shopflw.dto.cart.CartItemUpdateRequest;
import com.fsm.shopflw.dto.cart.CartResponse;
import com.fsm.shopflw.dto.cart.CouponApplyRequest;
import com.fsm.shopflw.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse getCart() {
        return cartService.getCurrentCart();
    }

    @PostMapping("/items")
    public CartResponse addItem(@Valid @RequestBody CartItemRequest request) {
        return cartService.addItem(request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(@PathVariable Long itemId, @Valid @RequestBody CartItemUpdateRequest request) {
        return cartService.updateItem(itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(@PathVariable Long itemId) {
        return cartService.removeItem(itemId);
    }

    @PostMapping("/coupon")
    public CartResponse applyCoupon(@Valid @RequestBody CouponApplyRequest request) {
        return cartService.applyCoupon(request.code());
    }

    @DeleteMapping("/coupon")
    public CartResponse removeCoupon() {
        return cartService.removeCoupon();
    }
}
