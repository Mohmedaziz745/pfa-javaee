package com.fsm.shopflw.repository;

import com.fsm.shopflw.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("select distinct p from Product p left join p.categories c " +
            "where lower(p.nom) like lower(concat('%', :query, '%')) " +
            "or lower(p.description) like lower(concat('%', :query, '%')) " +
            "or lower(c.nom) like lower(concat('%', :query, '%'))")
    Page<Product> search(String query, Pageable pageable);

    List<Product> findTop10ByActifTrueOrderBySalesCountDesc();
}
