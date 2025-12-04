package com.dimitrijevic175.product_service.service;

import com.dimitrijevic175.product_service.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    ProductResponse createProduct(CreateProductRequest request);
    ProductResponse getProductById(Long id);
    ProductResponse updateProduct(Long id, ProductUpdateRequest request);
    ProductResponse updateProductStatus(Long id, Boolean active);
    ProductStockResponse getProductStock(Long id);
    Page<ProductResponse> getProducts(ProductSearchRequest request, Pageable pageable);

}
