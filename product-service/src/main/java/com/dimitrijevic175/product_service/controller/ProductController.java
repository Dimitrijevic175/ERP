package com.dimitrijevic175.product_service.controller;

import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.dto.*;
import com.dimitrijevic175.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProductStatus(id, request.getActive()));
    }

    @GetMapping("/{id}/stock")
    public ResponseEntity<ProductStockResponse> getProductStock(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductStock(id));
    }

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String sku,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    ) {
        ProductSearchRequest req = new ProductSearchRequest();
        req.setName(name);
        req.setSku(sku);
        req.setCategoryId(categoryId);
        req.setBrand(brand);
        req.setPriceMin(priceMin);
        req.setPriceMax(priceMax);
        req.setActive(active);

        return ResponseEntity.ok(productService.getProducts(req, pageable));
    }

}
