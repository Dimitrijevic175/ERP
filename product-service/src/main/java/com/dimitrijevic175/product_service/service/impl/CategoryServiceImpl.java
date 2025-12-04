package com.dimitrijevic175.product_service.service.impl;

import com.dimitrijevic175.product_service.domain.Category;
import com.dimitrijevic175.product_service.dto.CategoryResponse;
import com.dimitrijevic175.product_service.dto.CategoryUpdateRequest;
import com.dimitrijevic175.product_service.dto.CreateCategoryRequest;
import com.dimitrijevic175.product_service.exceptions.CategoryDeletionNotAllowedException;
import com.dimitrijevic175.product_service.exceptions.CategoryNotFoundException;
import com.dimitrijevic175.product_service.mapper.CategoryMapper;
import com.dimitrijevic175.product_service.repository.CategoryRepository;
import com.dimitrijevic175.product_service.service.CategoryService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentCategoryId()));
            category.setParentCategory(parent);
        }

        Category saved = categoryRepository.save(category);
        return CategoryMapper.toResponse(saved);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryMapper::toResponse);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        Category parent = null;
        if (request.getParentCategoryId() != null) {

            if (request.getParentCategoryId().equals(id)) {
                throw new IllegalArgumentException("A category cannot be its own parent.");
            }

            parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.getParentCategoryId()));
        }

        CategoryMapper.updateEntity(category, request, parent);

        Category saved = categoryRepository.save(category);
        return CategoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getProducts().isEmpty()) {
            throw new CategoryDeletionNotAllowedException("Category contains products");
        }

        if (!category.getSubCategories().isEmpty()) {
            throw new CategoryDeletionNotAllowedException("Category contains subcategories");
        }

        categoryRepository.delete(category);
    }
}
