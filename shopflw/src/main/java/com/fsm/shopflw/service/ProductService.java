package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.product.ProductRequest;
import com.fsm.shopflw.dto.product.ProductResponse;
import com.fsm.shopflw.dto.product.ProductVariantRequest;
import com.fsm.shopflw.exception.ForbiddenException;
import com.fsm.shopflw.exception.NotFoundException;
import com.fsm.shopflw.model.Category;
import com.fsm.shopflw.model.Product;
import com.fsm.shopflw.model.ProductVariant;
import com.fsm.shopflw.model.User;
import com.fsm.shopflw.model.enums.Role;
import com.fsm.shopflw.repository.CategoryRepository;
import com.fsm.shopflw.repository.ProductRepository;
import com.fsm.shopflw.repository.ProductSpecifications;
import com.fsm.shopflw.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;
    private final SecurityFacade securityFacade;
    private final MapperService mapperService;

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, Long sellerId, Boolean promo, Pageable pageable) {
        Specification<Product> spec = Specification.where(ProductSpecifications.isActive())
                .and(ProductSpecifications.hasCategory(categoryId))
                .and(ProductSpecifications.minPrice(minPrice))
                .and(ProductSpecifications.maxPrice(maxPrice))
                .and(ProductSpecifications.hasSeller(sellerId))
                .and(ProductSpecifications.promo(promo));
        return productRepository.findAll(spec, pageable)
                .map(product -> mapperService.toProductResponse(
                        product,
                        reviewRepository.averageRating(product.getId()),
                        reviewRepository.findByProductIdAndApprouveTrueOrderByDateCreationDesc(product.getId())
                ));
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produit introuvable"));
        return mapperService.toProductResponse(
                product,
                reviewRepository.averageRating(product.getId()),
                reviewRepository.findByProductIdAndApprouveTrueOrderByDateCreationDesc(product.getId())
        );
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String q, Pageable pageable) {
        return productRepository.search(q, pageable)
                .map(product -> mapperService.toProductResponse(
                        product,
                        reviewRepository.averageRating(product.getId()),
                        reviewRepository.findByProductIdAndApprouveTrueOrderByDateCreationDesc(product.getId())
                ));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> topSelling() {
        return productRepository.findTop10ByActifTrueOrderBySalesCountDesc().stream()
                .map(product -> mapperService.toProductResponse(
                        product,
                        reviewRepository.averageRating(product.getId()),
                        reviewRepository.findByProductIdAndApprouveTrueOrderByDateCreationDesc(product.getId())
                ))
                .toList();
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        User current = securityFacade.currentUser();
        if (current.getRole() == Role.CUSTOMER) {
            throw new ForbiddenException("Seuls les vendeurs ou admins peuvent creer un produit");
        }
        Product product = Product.builder()
                .seller(current.getRole() == Role.ADMIN && request.categoryIds().isEmpty() ? current : current)
                .nom(request.nom())
                .description(request.description())
                .prix(request.prix())
                .prixPromo(request.prixPromo())
                .stock(request.stock())
                .actif(true)
                .images(request.images() == null ? List.of() : request.images())
                .categories(resolveCategories(request.categoryIds()))
                .build();
        attachVariants(product, request.variants());
        Product saved = productRepository.save(product);
        return getById(saved.getId());
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produit introuvable"));
        checkSellerAccess(product);
        product.setNom(request.nom());
        product.setDescription(request.description());
        product.setPrix(request.prix());
        product.setPrixPromo(request.prixPromo());
        product.setStock(request.stock());
        product.setImages(request.images() == null ? List.of() : request.images());
        product.setCategories(resolveCategories(request.categoryIds()));
        product.getVariants().clear();
        attachVariants(product, request.variants());
        return getById(product.getId());
    }

    @Transactional
    public void deactivate(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NotFoundException("Produit introuvable"));
        checkSellerAccess(product);
        product.setActif(false);
    }

    private Set<Category> resolveCategories(Set<Long> ids) {
        Set<Category> categories = new HashSet<>(categoryRepository.findAllById(ids));
        if (categories.size() != ids.size()) {
            throw new NotFoundException("Une ou plusieurs categories sont introuvables");
        }
        return categories;
    }

    private void attachVariants(Product product, List<ProductVariantRequest> variants) {
        if (variants == null) {
            return;
        }
        for (ProductVariantRequest request : variants) {
            product.getVariants().add(ProductVariant.builder()
                    .product(product)
                    .attribut(request.attribut())
                    .valeur(request.valeur())
                    .stockSupplementaire(request.stockSupplementaire())
                    .prixDelta(request.prixDelta() == null ? BigDecimal.ZERO : request.prixDelta())
                    .build());
        }
    }

    private void checkSellerAccess(Product product) {
        User current = securityFacade.currentUser();
        if (current.getRole() == Role.ADMIN) {
            return;
        }
        if (!product.getSeller().getId().equals(current.getId())) {
            throw new ForbiddenException("Acces refuse a ce produit");
        }
    }
}
