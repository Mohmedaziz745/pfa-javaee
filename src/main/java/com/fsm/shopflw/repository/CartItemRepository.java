package com.fsm.shopflw.repository;

import com.fsm.shopflw.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
