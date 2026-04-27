package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.review.ReviewRequest;
import com.fsm.shopflw.dto.review.ReviewResponse;
import com.fsm.shopflw.exception.BadRequestException;
import com.fsm.shopflw.exception.NotFoundException;
import com.fsm.shopflw.model.Product;
import com.fsm.shopflw.model.Review;
import com.fsm.shopflw.repository.OrderItemRepository;
import com.fsm.shopflw.repository.ProductRepository;
import com.fsm.shopflw.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final SecurityFacade securityFacade;
    private final MapperService mapperService;

    @Transactional
    public ReviewResponse create(ReviewRequest request) {
        Product product = productRepository.findById(request.productId()).orElseThrow(() -> new NotFoundException("Produit introuvable"));
        Long customerId = securityFacade.currentUser().getId();
        if (!orderItemRepository.existsVerifiedPurchase(customerId, product.getId())) {
            throw new BadRequestException("Avis reserve aux achats verifies");
        }
        if (reviewRepository.existsByCustomerIdAndProductId(customerId, product.getId())) {
            throw new BadRequestException("Un avis existe deja pour ce produit");
        }
        Review review = reviewRepository.save(Review.builder()
                .customer(securityFacade.currentUser())
                .product(product)
                .note(request.note())
                .commentaire(request.commentaire())
                .approuve(false)
                .build());
        return mapperService.toReviewResponse(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> productReviews(Long productId) {
        return reviewRepository.findByProductIdAndApprouveTrueOrderByDateCreationDesc(productId).stream()
                .map(mapperService::toReviewResponse)
                .toList();
    }

    @Transactional
    public ReviewResponse approve(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new NotFoundException("Avis introuvable"));
        review.setApprouve(true);
        return mapperService.toReviewResponse(review);
    }
}
