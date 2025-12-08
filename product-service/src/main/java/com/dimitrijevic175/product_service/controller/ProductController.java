package com.dimitrijevic175.product_service.controller;

import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.dto.*;
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

    @GetMapping("/low-stock")
    public ResponseEntity<Page<ProductResponse>> getLowStockProducts(Pageable pageable) {
        Page<ProductResponse> products = productService.getLowStockProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/{id}/increase-stock")
    public ResponseEntity<ProductResponse> increaseStock(@PathVariable Long id, @Valid @RequestBody IncreaseStockRequest request) {
        ProductResponse response = productService.increaseStock(id, request.getAmount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<ProductResponse> decreaseStock(@PathVariable Long id, @Valid @RequestBody DecreaseStockRequest request) {
        ProductResponse response = productService.decreaseStock(id, request.getAmount());
        return ResponseEntity.ok(response);
    }
    @PostMapping("/import")
    public ResponseEntity<ImportResult> importProducts(@RequestParam("file") MultipartFile file) {
        ImportResult result = productService.importProducts(file);
        return ResponseEntity.ok(result);
    }
}
