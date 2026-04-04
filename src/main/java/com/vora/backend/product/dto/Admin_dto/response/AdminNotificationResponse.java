package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNotificationResponse {
    private Long id;
    private String type;
    private String title;
    private String message;
    private String metaJson;
    private Boolean read;
    private LocalDateTime createdAt;
}
