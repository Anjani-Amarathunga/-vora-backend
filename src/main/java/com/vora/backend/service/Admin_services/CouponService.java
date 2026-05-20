package com.vora.backend.service.Admin_services;

import com.vora.backend.product.dto.Admin_dto.request.CouponRequest;
import com.vora.backend.product.dto.Admin_dto.response.CouponResponse;
import java.math.BigDecimal;
import java.util.List;

public interface CouponService {
    CouponResponse create(CouponRequest request);
    CouponResponse update(Long id, CouponRequest request);
    void delete(Long id);
    List<CouponResponse> getAll();
    CouponResponse getByCode(String code);
    BigDecimal validateAndCalculate(String code, BigDecimal orderAmount);
}
