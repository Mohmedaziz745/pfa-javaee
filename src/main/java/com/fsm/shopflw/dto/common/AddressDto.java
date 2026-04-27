package com.fsm.shopflw.dto.common;

public record AddressDto(
        Long id,
        String rue,
        String ville,
        String codePostal,
        String pays,
        boolean principal
) {
}
