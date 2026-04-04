package com.vora.backend.service.Admin_services.impl;


import com.vora.backend.product.dto.Admin_dto.request.CategoryRequest;
import com.vora.backend.product.dto.Admin_dto.response.CategoryResponse;
import com.vora.backend.user.Admin_entity.Category;
import com.vora.backend.exception.*;
import com.vora.backend.repository.Admin_repository.CategoryRepository;
import com.vora.backend.service.Admin_services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse create(CategoryRequest req) {
        if (categoryRepository.existsByNameIgnoreCase(req.getName()))
            throw new BadRequestException("Category already exists: " + req.getName());

        Category cat = Category.builder()
                .name(req.getName())
                .description(req.getDescription())
                .imageUrl(req.getImageUrl())
                .isActive(true)
                .build();
        return toResponse(categoryRepository.save(cat));
    }

    @Override
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        cat.setName(req.getName());
        cat.setDescription(req.getDescription());
        cat.setImageUrl(req.getImageUrl());
        return toResponse(categoryRepository.save(cat));
    }

    @Override
    public void delete(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        cat.setIsActive(false);
        categoryRepository.save(cat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getActive() {
        return categoryRepository.findByIsActiveTrue().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    private CategoryResponse toResponse(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.setId(c.getId());
        r.setName(c.getName());
        r.setDescription(c.getDescription());
        r.setImageUrl(c.getImageUrl());
        r.setIsActive(c.getIsActive());
        r.setCreatedAt(c.getCreatedAt());
        return r;
    }
}
