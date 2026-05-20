package com.vora.backend.service.Admin_services.impl;


import com.vora.backend.product.dto.Admin_dto.request.ProductRequest;
import com.vora.backend.product.dto.Admin_dto.request.StockUpdateRequest;
import com.vora.backend.product.dto.Admin_dto.response.ProductResponse;
import com.vora.backend.user.Admin_entity.*;
import com.vora.backend.exception.ResourceNotFoundException;
import com.vora.backend.repository.Admin_repository.*;
import com.vora.backend.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public ProductResponse create(ProductRequest req, List<MultipartFile> images) {
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Product product = Product.builder()
                .name(req.getName())
                .description(req.getDescription())
                .price(req.getPrice())
                .category(category)
                .stockQty(req.getStockQty())
                .isActive(true)
                .images(new ArrayList<>())
                .build();

        if (images != null && !images.isEmpty()) {
            List<ProductImage> productImages = uploadImages(images, product);
            if (!productImages.isEmpty()) productImages.get(0).setIsPrimary(true);
            product.setImages(productImages);
        }

        return toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse update(Long id, ProductRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setName(req.getName());
        product.setDescription(req.getDescription());
        product.setPrice(req.getPrice());
        product.setCategory(category);
        product.setStockQty(req.getStockQty());

        return toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setIsActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return toResponse(productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAll(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Pageable pageable) {
        return productRepository.search(query, pageable).map(this::toResponse);
    }

    @Override
    public ProductResponse updateStock(Long id, StockUpdateRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setStockQty(req.getStockQty());
        return toResponse(productRepository.save(product));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStock(int threshold) {
        return productRepository.findLowStockProducts(threshold)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void addImages(Long productId, List<MultipartFile> images) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.getImages().addAll(uploadImages(images, product));
        productRepository.save(product);
    }

    @Override
    public void deleteImage(Long imageId) {
        // Remove image by id from the product's image list
        productRepository.findAll().forEach(p -> {
            p.getImages().removeIf(img -> img.getId().equals(imageId));
            productRepository.save(p);
        });
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private List<ProductImage> uploadImages(List<MultipartFile> files, Product product) {
        List<ProductImage> result = new ArrayList<>();
        try {
            Path dir = Paths.get(uploadDir);
            Files.createDirectories(dir);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path dest = dir.resolve(filename);
                Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);
                result.add(ProductImage.builder()
                        .product(product)
                        .imageUrl("/uploads/" + filename)
                        .isPrimary(false)
                        .build());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to store image", e);
        }
        return result;
    }

    private ProductResponse toResponse(Product p) {
        ProductResponse r = new ProductResponse();
        r.setId(p.getId());
        r.setName(p.getName());
        r.setDescription(p.getDescription());
        r.setPrice(p.getPrice());
        r.setCategoryId(p.getCategory() != null ? p.getCategory().getId() : null);
        r.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : null);
        r.setStockQty(p.getStockQty());
        r.setIsActive(p.getIsActive());
        r.setImages(p.getImages().stream()
                .map(img -> new ProductResponse.ImageDto(img.getId(), img.getImageUrl(), img.getIsPrimary()))
                .collect(Collectors.toList()));
        r.setCreatedAt(p.getCreatedAt());
        return r;
    }
}
