package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;
import java.math.BigDecimal;
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
