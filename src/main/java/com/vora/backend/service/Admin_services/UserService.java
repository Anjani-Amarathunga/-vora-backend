package com.vora.backend.service.Admin_services;

import com.vora.backend.product.dto.Admin_dto.request.AdminCreateUserRequest;
import com.vora.backend.product.dto.Admin_dto.response.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse create(AdminCreateUserRequest request);
    UserResponse getById(Long id);
    void delete(Long id);
    UserResponse toggleActive(Long id);
    UserResponse updateRole(Long id, String role);
}
