package com.vora.backend.product.dto.Admin_dto.request;

import com.vora.backend.enums.DiscountType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
public class CouponRequest {
    @NotBlank private String code;
    @NotNull private DiscountType discountType;
    @NotNull @DecimalMin("0.01") private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private Integer maxUses;
    private LocalDateTime expiresAt;
    private Boolean isActive = true;
}
