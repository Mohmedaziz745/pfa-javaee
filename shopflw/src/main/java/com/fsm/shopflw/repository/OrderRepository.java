package com.fsm.shopflw.repository;

import com.fsm.shopflw.model.OrderEntity;
import com.fsm.shopflw.model.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> findByCustomerIdOrderByDateCommandeDesc(Long customerId);

    Optional<OrderEntity> findByIdAndCustomerId(Long id, Long customerId);

    @Query("select coalesce(sum(o.totalTTC), 0) from OrderEntity o where o.statut <> 'CANCELLED'")
    BigDecimal sumRevenue();

    long countByStatut(OrderStatus statut);
}
