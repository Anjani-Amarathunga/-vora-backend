package com.vora.backend.product.dto.Admin_dto.response;

import lombok.*;
import java.util.List;

@Data 
@AllArgsConstructor 
@NoArgsConstructor @Builder
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private List<String> roles;
}
