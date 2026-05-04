package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.cart.CartResponse;
import com.fsm.shopflw.dto.order.OrderResponse;
import com.fsm.shopflw.exception.BadRequestException;
import com.fsm.shopflw.exception.ForbiddenException;
import com.fsm.shopflw.exception.NotFoundException;
import com.fsm.shopflw.model.Address;
import com.fsm.shopflw.model.Cart;
import com.fsm.shopflw.model.CartItem;
import com.fsm.shopflw.model.Coupon;
import com.fsm.shopflw.model.OrderEntity;
import com.fsm.shopflw.model.OrderItem;
import com.fsm.shopflw.model.Product;
import com.fsm.shopflw.model.ProductVariant;
import com.fsm.shopflw.model.User;
import com.fsm.shopflw.model.enums.OrderStatus;
import com.fsm.shopflw.model.enums.Role;
import com.fsm.shopflw.repository.AddressRepository;
import com.fsm.shopflw.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final MapperService mapperService;
    private final SecurityFacade securityFacade;

    @Transactional
    public OrderResponse checkout(Long addressId) {
        User current = securityFacade.currentUser();
        Cart cart = cartService.getOrCreateCart();
        if (cart.getLignes().isEmpty()) {
            throw new BadRequestException("Le panier est vide");
        }

        Address address = addressRepository.findByIdAndUserId(addressId, current.getId())
                .orElseThrow(() -> new NotFoundException("Adresse de livraison introuvable"));

        CartResponse preview = cartService.toResponse(cart);
        OrderEntity order = OrderEntity.builder()
                .customer(current)
                .statut(OrderStatus.PENDING)
                .numeroCommande(generateOrderNumber())
                .adresseLivraison(address)
                .sousTotal(preview.sousTotal())
                .fraisLivraison(preview.fraisLivraison())
                .totalTTC(preview.totalTtc())
                .dateCommande(LocalDateTime.now())
                .isNew(true)
                .build();

        for (CartItem cartItem : cart.getLignes()) {
            Product product = cartItem.getProduct();
            ProductVariant variant = cartItem.getVariant();
            int available = product.getStock() + (variant != null ? variant.getStockSupplementaire() : 0);
            if (cartItem.getQuantite() > available) {
                throw new BadRequestException("Stock insuffisant pour " + product.getNom());
            }
            product.setStock(product.getStock() - cartItem.getQuantite());
            product.setSalesCount(product.getSalesCount() + cartItem.getQuantite());
            if (variant != null) {
                variant.setStockSupplementaire(Math.max(0, variant.getStockSupplementaire() - cartItem.getQuantite()));
            }
            order.getLignes().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .variant(variant)
                    .quantite(cartItem.getQuantite())
                    .prixUnitaire(mapperService.effectivePrice(product, variant))
                    .build());
        }

        Coupon coupon = cart.getCoupon();
        if (coupon != null) {
            coupon.setUsagesActuels(coupon.getUsagesActuels() + 1);
        }

        OrderEntity saved = orderRepository.save(order);
        cart.getLignes().clear();
        cart.setCoupon(null);
        cart.setDateModification(LocalDateTime.now());
        return mapperService.toOrderResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Commande introuvable"));
        assertOrderAccess(order);
        order.setNew(false);
        return mapperService.toOrderResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> myOrders() {
        return orderRepository.findByCustomerIdOrderByDateCommandeDesc(securityFacade.currentUser().getId()).stream()
                .map(mapperService::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> allOrders() {
        return orderRepository.findAll().stream().map(mapperService::toOrderResponse).toList();
    }

    @Transactional
    public OrderResponse updateStatus(Long id, OrderStatus status) {
        OrderEntity order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Commande introuvable"));
        User current = securityFacade.currentUser();
        if (current.getRole() == Role.SELLER) {
            boolean ownsLine = order.getLignes().stream().anyMatch(line -> line.getProduct().getSeller().getId().equals(current.getId()));
            if (!ownsLine) {
                throw new ForbiddenException("Commande non accessible pour ce vendeur");
            }
        } else if (current.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Mise a jour du statut interdite");
        }
        order.setStatut(status);
        order.setNew(true);
        return mapperService.toOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancel(Long id) {
        OrderEntity order = orderRepository.findByIdAndCustomerId(id, securityFacade.currentUser().getId())
                .orElseThrow(() -> new NotFoundException("Commande introuvable"));
        if (!(order.getStatut() == OrderStatus.PENDING || order.getStatut() == OrderStatus.PAID)) {
            throw new BadRequestException("Cette commande ne peut plus etre annulee");
        }
        order.setStatut(OrderStatus.REFUNDED);
        order.setNew(true);
        for (OrderItem line : order.getLignes()) {
            line.getProduct().setStock(line.getProduct().getStock() + line.getQuantite());
            if (line.getVariant() != null) {
                line.getVariant().setStockSupplementaire(line.getVariant().getStockSupplementaire() + line.getQuantite());
            }
        }
        return mapperService.toOrderResponse(order);
    }

    private void assertOrderAccess(OrderEntity order) {
        User current = securityFacade.currentUser();
        if (current.getRole() == Role.ADMIN) {
            return;
        }
        if (current.getRole() == Role.CUSTOMER && order.getCustomer().getId().equals(current.getId())) {
            return;
        }
        if (current.getRole() == Role.SELLER && order.getLignes().stream().anyMatch(line -> line.getProduct().getSeller().getId().equals(current.getId()))) {
            return;
        }
        throw new ForbiddenException("Acces refuse a cette commande");
    }

    private String generateOrderNumber() {
        return "ORD-" + LocalDateTime.now().getYear() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
