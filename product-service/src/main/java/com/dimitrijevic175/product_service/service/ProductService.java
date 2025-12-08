package com.dimitrijevic175.product_service.service;

import com.dimitrijevic175.product_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    ProductResponse updateProductStatus(Long id, Boolean active);
    ProductStockResponse getProductStock(Long id);
    Page<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable);
    Page<ProductResponse> getLowStockProducts(Pageable pageable);
    ProductResponse increaseStock(Long id, Integer amount);
    ProductResponse decreaseStock(Long id, Integer amount);
    ImportResult importProducts(MultipartFile file);
}
