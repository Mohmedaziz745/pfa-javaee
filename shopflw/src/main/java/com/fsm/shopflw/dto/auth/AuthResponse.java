package com.fsm.shopflw.dto.auth;

import com.fsm.shopflw.model.enums.Role;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        Long userId,
        String email,
        Role role
) {
}
