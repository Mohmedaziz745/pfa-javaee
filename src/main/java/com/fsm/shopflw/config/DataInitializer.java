package com.fsm.shopflw.config;

import com.fsm.shopflw.model.Address;
import com.fsm.shopflw.model.Cart;
import com.fsm.shopflw.model.Category;
import com.fsm.shopflw.model.Coupon;
import com.fsm.shopflw.model.Product;
import com.fsm.shopflw.model.ProductVariant;
import com.fsm.shopflw.model.SellerProfile;
import com.fsm.shopflw.model.User;
import com.fsm.shopflw.model.enums.CouponType;
import com.fsm.shopflw.model.enums.Role;
import com.fsm.shopflw.repository.CategoryRepository;
import com.fsm.shopflw.repository.CouponRepository;
import com.fsm.shopflw.repository.ProductRepository;
import com.fsm.shopflw.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               CategoryRepository categoryRepository,
                               ProductRepository productRepository,
                               CouponRepository couponRepository) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }

            userRepository.save(User.builder()
                    .email("admin@shopflow.local")
                    .motDePasse(passwordEncoder.encode("Admin123"))
                    .prenom("Admin")
                    .nom("Root")
                    .role(Role.ADMIN)
                    .actif(true)
                    .build());

            User seller = User.builder()
                    .email("seller@shopflow.local")
                    .motDePasse(passwordEncoder.encode("Seller123"))
                    .prenom("Sara")
                    .nom("Seller")
                    .role(Role.SELLER)
                    .actif(true)
                    .build();
            seller.setSellerProfile(SellerProfile.builder()
                    .user(seller)
                    .nomBoutique("Sara Store")
                    .description("Design-led essentials for tech, fashion, and daily carry.")
                    .logo("https://images.unsplash.com/photo-1523381210434-271e8be1f52b?auto=format&fit=crop&w=400&q=80")
                    .note(4.8)
                    .build());
            seller = userRepository.save(seller);

            User customer = User.builder()
                    .email("customer@shopflow.local")
                    .motDePasse(passwordEncoder.encode("Customer123"))
                    .prenom("Chris")
                    .nom("Buyer")
                    .role(Role.CUSTOMER)
                    .actif(true)
                    .build();
            customer.setCart(Cart.builder().customer(customer).dateModification(LocalDateTime.now()).build());
            customer.getAddresses().add(Address.builder()
                    .user(customer)
                    .rue("10 Rue Demo")
                    .ville("Paris")
                    .codePostal("75001")
                    .pays("France")
                    .principal(true)
                    .build());
            userRepository.save(customer);

            Category electronics = categoryRepository.save(Category.builder().nom("Electronics").description("Tech products").build());
            Category clothing = categoryRepository.save(Category.builder().nom("Clothing").description("Fashion").build());
            Category footwear = categoryRepository.save(Category.builder().nom("Footwear").description("Shoes and sneakers").build());
            Category accessories = categoryRepository.save(Category.builder().nom("Accessories").description("Bags and daily essentials").build());
            Category homeOffice = categoryRepository.save(Category.builder().nom("Home Office").description("Desk and workspace pieces").build());

            productRepository.save(createProduct(
                    seller,
                    "Noise Cancelling Headphones",
                    "Wireless over-ear headphones with deep bass, active noise cancellation, and all-day comfort.",
                    BigDecimal.valueOf(299.90),
                    BigDecimal.valueOf(249.90),
                    20,
                    19L,
                    List.of(
                            "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1484704849700-f032a568e944?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(electronics),
                    List.of(
                            variant("Color", "Matte Black", 5, BigDecimal.ZERO),
                            variant("Color", "Sand", 3, BigDecimal.valueOf(10))
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Cotton T-Shirt",
                    "Heavyweight premium cotton T-shirt with a relaxed cut designed for everyday wear.",
                    BigDecimal.valueOf(39.90),
                    BigDecimal.valueOf(29.90),
                    50,
                    31L,
                    List.of(
                            "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1503341504253-dff4815485f1?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(clothing),
                    List.of(
                            variant("Size", "M", 10, BigDecimal.ZERO),
                            variant("Size", "L", 12, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Retro Running Sneakers",
                    "Lightweight everyday sneakers with cushioned soles and a vintage sport silhouette.",
                    BigDecimal.valueOf(119.90),
                    BigDecimal.valueOf(89.90),
                    24,
                    42L,
                    List.of(
                            "https://images.unsplash.com/photo-1542291026-7eec264c27ff?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1549298916-b41d501d3772?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(footwear, clothing),
                    List.of(
                            variant("Size", "42", 6, BigDecimal.ZERO),
                            variant("Size", "43", 8, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Leather Crossbody Bag",
                    "Structured leather bag with a compact silhouette and soft lining for essentials.",
                    BigDecimal.valueOf(149.90),
                    BigDecimal.valueOf(119.90),
                    14,
                    16L,
                    List.of(
                            "https://images.unsplash.com/photo-1548036328-c9fa89d128fa?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1594223274512-ad4803739b7c?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(accessories),
                    List.of(
                            variant("Color", "Cognac", 4, BigDecimal.ZERO),
                            variant("Color", "Black", 4, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Minimal Desk Lamp",
                    "Warm-tone LED desk lamp with dimmable brightness and a compact aluminum body.",
                    BigDecimal.valueOf(79.90),
                    null,
                    32,
                    9L,
                    List.of(
                            "https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(homeOffice),
                    List.of(
                            variant("Finish", "Graphite", 7, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Smartwatch S4",
                    "Fitness-ready smartwatch with AMOLED display, sleep tracking, and week-long battery life.",
                    BigDecimal.valueOf(229.90),
                    BigDecimal.valueOf(199.90),
                    18,
                    27L,
                    List.of(
                            "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1434494878577-86c23bcb06b9?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(electronics, accessories),
                    List.of(
                            variant("Strap", "Black Silicone", 6, BigDecimal.ZERO),
                            variant("Strap", "Stone Nylon", 5, BigDecimal.valueOf(15))
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Travel Weekender Bag",
                    "Canvas weekender bag with leather handles and a water-resistant interior lining.",
                    BigDecimal.valueOf(189.90),
                    BigDecimal.valueOf(149.90),
                    12,
                    13L,
                    List.of(
                            "https://images.unsplash.com/photo-1547949003-9792a18a2601?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(accessories),
                    List.of(
                            variant("Color", "Olive", 4, BigDecimal.ZERO),
                            variant("Color", "Black", 3, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Wool Blend Overshirt",
                    "Layer-ready overshirt in a soft wool blend with a relaxed fit and chest pockets.",
                    BigDecimal.valueOf(139.90),
                    BigDecimal.valueOf(109.90),
                    22,
                    17L,
                    List.of(
                            "https://images.unsplash.com/photo-1523398002811-999ca8dec234?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1496747611176-843222e1e57c?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(clothing),
                    List.of(
                            variant("Size", "M", 6, BigDecimal.ZERO),
                            variant("Size", "L", 6, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Ceramic Pour-Over Set",
                    "Minimal coffee kit with dripper, mug, and matching serving tray for morning rituals.",
                    BigDecimal.valueOf(69.90),
                    null,
                    28,
                    8L,
                    List.of(
                            "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1517701604599-bb29b565090c?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(homeOffice, accessories),
                    List.of(
                            variant("Color", "Cream", 8, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Bluetooth Speaker Mini",
                    "Compact portable speaker with punchy audio, IPX7 splash resistance, and USB-C charging.",
                    BigDecimal.valueOf(89.90),
                    BigDecimal.valueOf(69.90),
                    30,
                    23L,
                    List.of(
                            "https://images.unsplash.com/photo-1589003077984-894e133dabab?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1608043152269-423dbba4e7e1?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(electronics),
                    List.of(
                            variant("Color", "Charcoal", 10, BigDecimal.ZERO),
                            variant("Color", "Coral", 7, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Performance Hoodie",
                    "Soft brushed hoodie with lightweight stretch fabric for daily wear and training.",
                    BigDecimal.valueOf(84.90),
                    BigDecimal.valueOf(64.90),
                    34,
                    26L,
                    List.of(
                            "https://images.unsplash.com/photo-1578587018452-892bacefd3f2?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1507679799987-c73779587ccf?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(clothing),
                    List.of(
                            variant("Size", "M", 8, BigDecimal.ZERO),
                            variant("Size", "XL", 7, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Standing Desk Mat",
                    "Supportive anti-fatigue mat designed for standing desks and home office setups.",
                    BigDecimal.valueOf(59.90),
                    null,
                    26,
                    11L,
                    List.of(
                            "https://images.unsplash.com/photo-1497366754035-f200968a6e72?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1497366412874-3415097a27e7?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(homeOffice),
                    List.of(
                            variant("Size", "Standard", 10, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Leather Wallet Slim",
                    "Full-grain leather wallet with six card slots and a low-profile folded silhouette.",
                    BigDecimal.valueOf(54.90),
                    BigDecimal.valueOf(39.90),
                    40,
                    29L,
                    List.of(
                            "https://images.unsplash.com/photo-1627123424574-724758594e93?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(accessories),
                    List.of(
                            variant("Color", "Brown", 10, BigDecimal.ZERO),
                            variant("Color", "Black", 10, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Trail Cap",
                    "Lightweight five-panel cap with breathable mesh and a flexible brim for active days.",
                    BigDecimal.valueOf(34.90),
                    null,
                    44,
                    14L,
                    List.of(
                            "https://images.unsplash.com/photo-1521369909029-2afed882baee?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(accessories, clothing),
                    List.of(
                            variant("Color", "Stone", 12, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Mirrorless Camera Strap",
                    "Padded adjustable strap built for compact cameras with premium hardware and quick release clips.",
                    BigDecimal.valueOf(49.90),
                    BigDecimal.valueOf(34.90),
                    20,
                    10L,
                    List.of(
                            "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(accessories, electronics),
                    List.of(
                            variant("Color", "Black", 6, BigDecimal.ZERO),
                            variant("Color", "Tan", 5, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Desk Organizer Tray",
                    "Solid oak tray for pens, keys, earbuds, and the small items that clutter a workday.",
                    BigDecimal.valueOf(44.90),
                    null,
                    24,
                    7L,
                    List.of(
                            "https://images.unsplash.com/photo-1515879218367-8466d910aaa4?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1498050108023-c5249f4df085?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(homeOffice),
                    List.of(
                            variant("Finish", "Natural Oak", 8, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Merino Beanie",
                    "Ribbed merino wool beanie with soft stretch and lightweight warmth.",
                    BigDecimal.valueOf(29.90),
                    BigDecimal.valueOf(19.90),
                    35,
                    18L,
                    List.of(
                            "https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(clothing, accessories),
                    List.of(
                            variant("Color", "Navy", 10, BigDecimal.ZERO),
                            variant("Color", "Charcoal", 10, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Urban Commuter Backpack",
                    "Structured backpack with laptop sleeve, hidden pocket, and weather-ready shell.",
                    BigDecimal.valueOf(159.90),
                    BigDecimal.valueOf(129.90),
                    16,
                    21L,
                    List.of(
                            "https://images.unsplash.com/photo-1500534314209-a25ddb2bd429?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1491637639811-60e2756cc1c7?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(accessories, homeOffice),
                    List.of(
                            variant("Color", "Graphite", 5, BigDecimal.ZERO),
                            variant("Color", "Sand", 4, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "USB-C Dock Pro",
                    "Seven-port aluminum dock with HDMI, SD, ethernet, and pass-through charging for hybrid work.",
                    BigDecimal.valueOf(129.90),
                    BigDecimal.valueOf(99.90),
                    19,
                    24L,
                    List.of(
                            "https://images.unsplash.com/photo-1517430816045-df4b7de11d1d?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1518770660439-4636190af475?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(electronics, homeOffice),
                    List.of(
                            variant("Version", "7-in-1", 7, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Slip-On Canvas Shoes",
                    "Easy everyday slip-ons with textured rubber soles and breathable canvas uppers.",
                    BigDecimal.valueOf(74.90),
                    BigDecimal.valueOf(54.90),
                    27,
                    15L,
                    List.of(
                            "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1463100099107-aa0980c362e6?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(footwear),
                    List.of(
                            variant("Size", "41", 6, BigDecimal.ZERO),
                            variant("Size", "42", 6, BigDecimal.ZERO)
                    )
            ));

            productRepository.save(createProduct(
                    seller,
                    "Pocket Notebook Set",
                    "Set of three ruled notebooks with stitched binding and soft recycled covers.",
                    BigDecimal.valueOf(19.90),
                    null,
                    60,
                    12L,
                    List.of(
                            "https://images.unsplash.com/photo-1531346680769-a1d79b57de5c?auto=format&fit=crop&w=900&q=80",
                            "https://images.unsplash.com/photo-1517842645767-c639042777db?auto=format&fit=crop&w=900&q=80"
                    ),
                    Set.of(homeOffice),
                    List.of(
                            variant("Pack", "3 notebooks", 20, BigDecimal.ZERO)
                    )
            ));

            couponRepository.save(Coupon.builder()
                    .code("WELCOME10")
                    .type(CouponType.PERCENT)
                    .valeur(BigDecimal.TEN)
                    .dateExpiration(LocalDateTime.now().plusMonths(6))
                    .usagesMax(100)
                    .actif(true)
                    .build());
        };
    }

    private Product createProduct(User seller,
                                  String nom,
                                  String description,
                                  BigDecimal prix,
                                  BigDecimal prixPromo,
                                  int stock,
                                  long salesCount,
                                  List<String> images,
                                  Set<Category> categories,
                                  List<ProductVariant> variants) {
        Product product = Product.builder()
                .seller(seller)
                .nom(nom)
                .description(description)
                .prix(prix)
                .prixPromo(prixPromo)
                .stock(stock)
                .actif(true)
                .salesCount(salesCount)
                .images(images)
                .categories(categories)
                .build();
        variants.forEach(variant -> {
            variant.setProduct(product);
            product.getVariants().add(variant);
        });
        return product;
    }

    private ProductVariant variant(String attribut, String valeur, int stockSupplementaire, BigDecimal prixDelta) {
        return ProductVariant.builder()
                .attribut(attribut)
                .valeur(valeur)
                .stockSupplementaire(stockSupplementaire)
                .prixDelta(prixDelta)
                .build();
    }
}
