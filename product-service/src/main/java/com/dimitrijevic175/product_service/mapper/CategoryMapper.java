package com.dimitrijevic175.product_service.mapper;

import com.dimitrijevic175.product_service.domain.Category;
import com.dimitrijevic175.product_service.dto.CategoryResponse;
import com.dimitrijevic175.product_service.dto.CategoryUpdateRequest;

public class CategoryMapper {

    public static CategoryResponse toResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .parentCategoryId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .active(category.getActive())
                .build();
    }

    public static void updateEntity(Category category, CategoryUpdateRequest request, Category parentCategory) {
        if (request.getName() != null) category.setName(request.getName());
        if (request.getDescription() != null) category.setDescription(request.getDescription());
        if (request.getActive() != null) category.setActive(request.getActive());
        if (parentCategory != null) category.setParentCategory(parentCategory);
    }
}
