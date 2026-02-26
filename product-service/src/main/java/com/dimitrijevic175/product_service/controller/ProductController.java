package com.dimitrijevic175.product_service.controller;

import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.dto.*;
import com.dimitrijevic175.product_service.security.CheckSecurity;
import com.dimitrijevic175.product_service.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PRODUCT"})
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.createProduct(request));
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PROCUREMENT","SALES","PRODUCT"})
    @GetMapping("/{id}/minQuantity")
    public ResponseEntity<Integer> getProductMinQuantity(@PathVariable Long id) {
        Integer minQuantity = productService.getMinQuantity(id);
        if (minQuantity == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(minQuantity);
    }

    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PROCUREMENT","SALES","PRODUCT"})
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PRODUCT"})
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        ProductResponse response = productService.updateProduct(id, request);
        return ResponseEntity.ok(response);
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PRODUCT"})
    @PutMapping("/{id}/status")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @PathVariable Long id,
            @Valid @RequestBody ProductStatusUpdateRequest request) {
        return ResponseEntity.ok(productService.updateProductStatus(id, request.getActive()));
    }

    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PROCUREMENT","SALES","PRODUCT"})
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
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PRODUCT"})
    @PostMapping("/import")
    public ResponseEntity<ImportResult> importProducts(@RequestParam("file") MultipartFile file) {
        ImportResult result = productService.importProducts(file);
        return ResponseEntity.ok(result);
    }
}
