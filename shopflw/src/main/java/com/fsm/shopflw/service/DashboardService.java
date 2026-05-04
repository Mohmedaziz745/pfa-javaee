package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.dashboard.AdminDashboardResponse;
import com.fsm.shopflw.dto.dashboard.SellerDashboardResponse;
import com.fsm.shopflw.model.OrderItem;
import com.fsm.shopflw.model.Product;
import com.fsm.shopflw.repository.OrderItemRepository;
import com.fsm.shopflw.repository.OrderRepository;
import com.fsm.shopflw.repository.ProductRepository;
import com.fsm.shopflw.repository.SellerProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final SellerProfileRepository sellerProfileRepository;
    private final SecurityFacade securityFacade;

    @Transactional(readOnly = true)
    public AdminDashboardResponse adminDashboard() {
        return new AdminDashboardResponse(
                orderRepository.sumRevenue(),
                orderRepository.count(),
                orderRepository.countByStatut(com.fsm.shopflw.model.enums.OrderStatus.PENDING),
                productRepository.findTop10ByActifTrueOrderBySalesCountDesc().stream().map(Product::getNom).toList(),
                sellerProfileRepository.findAll().stream()
                        .sorted(Comparator.comparing(profile -> profile.getNote() == null ? 0.0 : profile.getNote(), Comparator.reverseOrder()))
                        .limit(5)
                        .map(profile -> profile.getNomBoutique())
                        .toList(),
                orderRepository.findAll().stream()
                        .sorted(Comparator.comparing(order -> order.getDateCommande(), Comparator.reverseOrder()))
                        .limit(5)
                        .map(order -> order.getNumeroCommande() + " - " + order.getStatut())
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public SellerDashboardResponse sellerDashboard() {
        Long sellerId = securityFacade.currentUser().getId();
        List<OrderItem> orderItems = orderItemRepository.findBySellerId(sellerId);
        BigDecimal revenus = orderItems.stream()
                .map(item -> item.getPrixUnitaire().multiply(BigDecimal.valueOf(item.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long commandesEnAttente = orderItems.stream().filter(item -> item.getOrder().getStatut() == com.fsm.shopflw.model.enums.OrderStatus.PENDING).count();
        return new SellerDashboardResponse(
                revenus,
                orderItems.stream().map(item -> item.getOrder().getId()).distinct().count(),
                commandesEnAttente,
                productRepository.findAll().stream()
                        .filter(product -> product.getSeller().getId().equals(sellerId) && product.getStock() <= 5)
                        .map(product -> product.getNom() + " (stock " + product.getStock() + ")")
                        .toList()
        );
    }
}
