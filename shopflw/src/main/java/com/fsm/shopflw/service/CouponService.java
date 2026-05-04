package com.fsm.shopflw.service;

import com.fsm.shopflw.dto.coupon.CouponRequest;
import com.fsm.shopflw.dto.coupon.CouponResponse;
import com.fsm.shopflw.exception.BadRequestException;
import com.fsm.shopflw.exception.NotFoundException;
import com.fsm.shopflw.model.Coupon;
import com.fsm.shopflw.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;
    private final MapperService mapperService;

    @Transactional(readOnly = true)
    public CouponResponse validate(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new NotFoundException("Coupon introuvable"));
        return mapperService.toCouponResponse(coupon, isValid(coupon));
    }

    @Transactional
    public CouponResponse create(CouponRequest request) {
        Coupon coupon = couponRepository.save(Coupon.builder()
                .code(request.code().toUpperCase())
                .type(request.type())
                .valeur(request.valeur())
                .dateExpiration(request.dateExpiration())
                .usagesMax(request.usagesMax())
                .actif(request.actif())
                .build());
        return mapperService.toCouponResponse(coupon, isValid(coupon));
    }

    @Transactional
    public CouponResponse update(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id).orElseThrow(() -> new NotFoundException("Coupon introuvable"));
        coupon.setCode(request.code().toUpperCase());
        coupon.setType(request.type());
        coupon.setValeur(request.valeur());
        coupon.setDateExpiration(request.dateExpiration());
        coupon.setUsagesMax(request.usagesMax());
        coupon.setActif(request.actif());
        return mapperService.toCouponResponse(coupon, isValid(coupon));
    }

    @Transactional
    public void delete(Long id) {
        couponRepository.delete(couponRepository.findById(id).orElseThrow(() -> new NotFoundException("Coupon introuvable")));
    }

    public Coupon requireValidCoupon(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new NotFoundException("Coupon introuvable"));
        if (!isValid(coupon)) {
            throw new BadRequestException("Coupon invalide ou expire");
        }
        return coupon;
    }

    public boolean isValid(Coupon coupon) {
        return coupon.isActif()
                && (coupon.getDateExpiration() == null || coupon.getDateExpiration().isAfter(LocalDateTime.now()))
                && (coupon.getUsagesMax() == null || coupon.getUsagesActuels() < coupon.getUsagesMax());
    }
}
