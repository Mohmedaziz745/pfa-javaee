package com.fsm.shopflw.repository;

import com.fsm.shopflw.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("select oi from OrderItem oi where oi.product.seller.id = :sellerId")
    List<OrderItem> findBySellerId(Long sellerId);

    @Query("select count(oi) > 0 from OrderItem oi where oi.order.customer.id = :customerId and oi.product.id = :productId")
    boolean existsVerifiedPurchase(Long customerId, Long productId);
}
