package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.auth.AuthResponse;
import com.fsm.shopflw.dto.auth.LoginRequest;
import com.fsm.shopflw.dto.auth.RefreshRequest;
import com.fsm.shopflw.dto.auth.RegisterRequest;
import com.fsm.shopflw.exception.BadRequestException;
import com.fsm.shopflw.model.Address;
import com.fsm.shopflw.model.Cart;
import com.fsm.shopflw.model.RefreshToken;
import com.fsm.shopflw.model.SellerProfile;
import com.fsm.shopflw.model.User;
import com.fsm.shopflw.model.enums.Role;
import com.fsm.shopflw.repository.CartRepository;
import com.fsm.shopflw.repository.RefreshTokenRepository;
import com.fsm.shopflw.repository.UserRepository;
import com.fsm.shopflw.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().toLowerCase();
        if (request.role() == Role.ADMIN) {
            throw new BadRequestException("L'inscription ADMIN n'est pas autorisee");
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email deja utilise");
        }

        User user = User.builder()
                .email(normalizedEmail)
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .prenom(request.prenom())
                .nom(request.nom())
                .role(request.role())
                .actif(true)
                .build();

        if (request.role() == Role.SELLER) {
            if (request.nomBoutique() == null || request.nomBoutique().isBlank()) {
                throw new BadRequestException("Le nom de boutique est obligatoire pour un vendeur");
            }
            SellerProfile profile = SellerProfile.builder()
                    .user(user)
                    .nomBoutique(request.nomBoutique())
                    .description(request.descriptionBoutique())
                    .logo(request.logoBoutique())
                    .note(0.0)
                    .build();
            user.setSellerProfile(profile);
        } else {
            Cart cart = Cart.builder().customer(user).dateModification(LocalDateTime.now()).build();
            user.setCart(cart);
            user.getAddresses().add(Address.builder()
                    .user(user)
                    .rue("1 Demo Street")
                    .ville("Paris")
                    .codePostal("75001")
                    .pays("France")
                    .principal(true)
                    .build());
        }

        User saved = userRepository.save(user);
        if (saved.getRole() == Role.CUSTOMER && saved.getCart() != null) {
            cartRepository.save(saved.getCart());
        }
        return issueTokens(saved);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().toLowerCase();
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, request.motDePasse()));
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("Identifiants invalides"));
        if (!user.isActif()) {
            throw new BadRequestException("Compte desactive");
        }
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new BadRequestException("Refresh token invalide"));
        if (refreshToken.isRevoked() || refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expire ou revoque");
        }
        if (!jwtService.isValid(refreshToken.getToken(), refreshToken.getUser().getEmail())) {
            throw new BadRequestException("Refresh token invalide");
        }
        refreshToken.setRevoked(true);
        return issueTokens(refreshToken.getUser());
    }

    @Transactional
    public void logout(RefreshRequest request) {
        refreshTokenRepository.findByToken(request.refreshToken()).ifPresent(token -> token.setRevoked(true));
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getEmail(), Map.of(
                "role", user.getRole().name(),
                "userId", user.getId()
        ));
        String refreshTokenValue = jwtService.generateRefreshToken(user.getEmail());
        RefreshToken refreshToken = RefreshToken.builder()
                .token(refreshTokenValue)
                .user(user)
                .expiresAt(jwtService.extractExpiration(refreshTokenValue).atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return new AuthResponse(accessToken, refreshTokenValue, user.getId(), user.getEmail(), user.getRole());
    }
}
