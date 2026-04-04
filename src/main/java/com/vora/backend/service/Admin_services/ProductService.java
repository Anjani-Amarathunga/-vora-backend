package com.vora.backend.service.Admin_services;

import com.vora.backend.product.dto.Admin_dto.request.ProductRequest;
import com.vora.backend.product.dto.Admin_dto.request.StockUpdateRequest;
import com.vora.backend.product.dto.Admin_dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request, List<MultipartFile> images);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
    ProductResponse getById(Long id);
    Page<ProductResponse> getAll(Pageable pageable);
    Page<ProductResponse> search(String query, Pageable pageable);
    ProductResponse updateStock(Long id, StockUpdateRequest request);
    List<ProductResponse> getLowStock(int threshold);
    void addImages(Long productId, List<MultipartFile> images);
    void deleteImage(Long imageId);
}
