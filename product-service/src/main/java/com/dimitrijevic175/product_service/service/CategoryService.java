package com.dimitrijevic175.product_service.service;

import com.dimitrijevic175.product_service.dto.CategoryResponse;
import com.dimitrijevic175.product_service.dto.CategoryUpdateRequest;
import com.dimitrijevic175.product_service.dto.CreateCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CategoryService {
    CategoryResponse createCategory(CreateCategoryRequest request);
    Page<CategoryResponse> getAllCategories(Pageable pageable);
    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request);
    void deleteCategory(Long id);
}
