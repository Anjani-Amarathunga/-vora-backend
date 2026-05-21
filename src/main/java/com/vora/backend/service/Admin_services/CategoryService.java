package com.vora.backend.service.Admin_services;

import com.vora.backend.product.dto.Admin_dto.request.CategoryRequest;
import com.vora.backend.product.dto.Admin_dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
    List<CategoryResponse> getAll();
    List<CategoryResponse> getActive();
}
