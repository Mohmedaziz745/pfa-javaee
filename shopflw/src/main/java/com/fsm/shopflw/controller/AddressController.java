package com.fsm.shopflw.controller;

import com.fsm.shopflw.dto.common.AddressDto;
import com.fsm.shopflw.repository.AddressRepository;
import com.fsm.shopflw.service.MapperService;
import com.fsm.shopflw.service.SecurityFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressRepository addressRepository;
    private final MapperService mapperService;
    private final SecurityFacade securityFacade;

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<AddressDto> myAddresses() {
        return addressRepository.findByUserId(securityFacade.currentUser().getId()).stream()
                .map(mapperService::toAddressDto)
                .toList();
    }
}
