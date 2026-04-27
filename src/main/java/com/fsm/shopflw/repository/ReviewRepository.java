package com.fsm.shopflw.repository;

import com.fsm.shopflw.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndApprouveTrueOrderByDateCreationDesc(Long productId);

    Optional<Review> findByIdAndApprouveTrue(Long id);

    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    @Query("select coalesce(avg(r.note), 0) from Review r where r.product.id = :productId and r.approuve = true")
    Double averageRating(Long productId);

    List<Review> findByCustomerIdOrderByDateCreationDesc(Long customerId);
}
