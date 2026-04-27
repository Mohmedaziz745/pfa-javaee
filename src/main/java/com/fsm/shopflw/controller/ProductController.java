package com.fsm.shopflw.controller;

import com.fsm.shopflw.dto.product.ProductRequest;
import com.fsm.shopflw.dto.product.ProductResponse;
import com.fsm.shopflw.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> list(
            @RequestParam(required = false) Long categorie,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax,
            @RequestParam(required = false) Long vendeur,
            @RequestParam(required = false) Boolean promo,
            Pageable pageable
    ) {
        return productService.list(categorie, prixMin, prixMax, vendeur, promo, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.getById(id);
    }

    @PostMapping
    public ProductResponse create(@Valid @RequestBody ProductRequest request) {
        return productService.create(request);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void deactivate(@PathVariable Long id) {
        productService.deactivate(id);
    }

    @GetMapping("/search")
    public Page<ProductResponse> search(@RequestParam String q, Pageable pageable) {
        return productService.search(q, pageable);
    }

    @GetMapping("/top-selling")
    public List<ProductResponse> topSelling() {
        return productService.topSelling();
    }
}
