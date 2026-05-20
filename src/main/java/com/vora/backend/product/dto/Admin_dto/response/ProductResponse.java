package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Long categoryId;
    private String categoryName;
    private Integer stockQty;
    private Boolean isActive;
    private List<ImageDto> images;
    private LocalDateTime createdAt;

    @Data
    @AllArgsConstructor
    public static class ImageDto {
        private Long id;
        private String imageUrl;
        private Boolean isPrimary;
    }
}
