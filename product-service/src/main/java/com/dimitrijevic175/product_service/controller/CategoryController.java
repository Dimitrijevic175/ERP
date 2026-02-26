package com.dimitrijevic175.product_service.controller;

import com.dimitrijevic175.product_service.dto.CategoryResponse;
import com.dimitrijevic175.product_service.dto.CategoryUpdateRequest;
import com.dimitrijevic175.product_service.dto.CreateCategoryRequest;
import com.dimitrijevic175.product_service.security.CheckSecurity;
import com.dimitrijevic175.product_service.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/category")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PRODUCT"})
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.ok(response);
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PROCUREMENT","SALES","PRODUCT"})
    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.getAllCategories(pageable));
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PRODUCT"})
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryUpdateRequest request
    ) {
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }
    @CheckSecurity(roles = {"ADMIN","WAREHOUSE","PRODUCT"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
