package com.fsm.shopflw.service;

import com.fsm.shopflw.exception.BadRequestException;
import com.fsm.shopflw.exception.NotFoundException;
import com.fsm.shopflw.model.User;
import com.fsm.shopflw.model.enums.Role;
import com.fsm.shopflw.repository.UserRepository;
import com.fsm.shopflw.security.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityFacade {

    private final UserRepository userRepository;

    public User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AppUserDetails details)) {
            throw new BadRequestException("Utilisateur non authentifie");
        }
        return userRepository.findById(details.getUser().getId())
                .orElseThrow(() -> new NotFoundException("Utilisateur introuvable"));
    }

    public void requireRole(Role role) {
        if (currentUser().getRole() != role) {
            throw new BadRequestException("Role insuffisant");
        }
    }
}
