package com.vora.backend.service.Admin_services.impl;


import com.vora.backend.product.dto.Admin_dto.request.CouponRequest;
import com.vora.backend.product.dto.Admin_dto.response.CouponResponse;
import com.vora.backend.user.Admin_entity.Coupon;
import com.vora.backend.exception.BadRequestException;
import com.vora.backend.exception.ResourceNotFoundException;
import com.vora.backend.repository.Admin_repository.CouponRepository;
import com.vora.backend.service.Admin_services.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public CouponResponse create(CouponRequest req) {
        if (couponRepository.existsByCodeIgnoreCase(req.getCode()))
            throw new BadRequestException("Coupon code already exists");

        Coupon coupon = Coupon.builder()
                .code(req.getCode().toUpperCase())
                .discountType(req.getDiscountType())
                .discountValue(req.getDiscountValue())
                .minOrderAmount(req.getMinOrderAmount() != null ? req.getMinOrderAmount() : BigDecimal.ZERO)
                .maxUses(req.getMaxUses())
                .expiresAt(req.getExpiresAt())
                .isActive(true)
                .usedCount(0)
                .build();

        return toResponse(couponRepository.save(coupon));
    }

    @Override
    public CouponResponse update(Long id, CouponRequest req) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setDiscountType(req.getDiscountType());
        coupon.setDiscountValue(req.getDiscountValue());
        coupon.setMinOrderAmount(req.getMinOrderAmount() != null ? req.getMinOrderAmount() : BigDecimal.ZERO);
        coupon.setMaxUses(req.getMaxUses());
        coupon.setExpiresAt(req.getExpiresAt());
        coupon.setIsActive(req.getIsActive());
        return toResponse(couponRepository.save(coupon));
    }

    @Override
    public void delete(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setIsActive(false);
        couponRepository.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAll() {
        return couponRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getByCode(String code) {
        return toResponse(couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal validateAndCalculate(String code, BigDecimal orderAmount) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new BadRequestException("Invalid coupon code"));
        if (!coupon.isValid(orderAmount))
            throw new BadRequestException("Coupon is not applicable to this order");
        return coupon.calculateDiscount(orderAmount);
    }

    private CouponResponse toResponse(Coupon c) {
        CouponResponse r = new CouponResponse();
        r.setId(c.getId());
        r.setCode(c.getCode());
        r.setDiscountType(c.getDiscountType());
        r.setDiscountValue(c.getDiscountValue());
        r.setMinOrderAmount(c.getMinOrderAmount());
        r.setMaxUses(c.getMaxUses());
        r.setUsedCount(c.getUsedCount());
        r.setExpiresAt(c.getExpiresAt());
        r.setIsActive(c.getIsActive());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}
