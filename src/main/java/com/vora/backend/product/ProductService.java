package com.vora.backend.product;

import com.vora.backend.product.dto.ProductRequest;
import com.vora.backend.product.dto.ProductResponse;
import com.vora.backend.product.dto.Admin_dto.request.StockUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

// Re-export ProductService from Admin_services and add adapter methods
public interface ProductService extends com.vora.backend.service.Admin_services.ProductService {
    
    // Adapter methods for ProductController compatibility
    default ProductResponse createProduct(ProductRequest request) {
        return (ProductResponse) create((com.vora.backend.product.dto.Admin_dto.request.ProductRequest) request, null);
    }
    
    default ProductResponse updateProduct(Long id, ProductRequest request) {
        return (ProductResponse) update(id, (com.vora.backend.product.dto.Admin_dto.request.ProductRequest) request);
    }
    
    default void deleteProduct(Long id) {
        delete(id);
    }
    
    default ProductResponse getProductById(Long id) {
        return (ProductResponse) getById(id);
    }
    
    default List<ProductResponse> getAllProducts() {
        return (List<ProductResponse>) (List<?>) getAll(Pageable.unpaged()).getContent();
    }
    
    default List<ProductResponse> searchByName(String query) {
        return (List<ProductResponse>) (List<?>) search(query, Pageable.unpaged()).getContent();
    }
    
    default List<ProductResponse> filterByPrice(BigDecimal minPrice, BigDecimal maxPrice) {
        // This method needs to be implemented based on repository support
        return getAllProducts(); // Placeholder
    }
}
