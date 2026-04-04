package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Boolean isActive;
    private List<String> roles;
    private LocalDateTime createdAt;
}
