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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    private static final Logger logger = LogManager.getLogger(CategoryServiceImpl.class);


    @Override
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        logger.info("Creating category with name='{}'", request.getName());

        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());

        if (request.getParentCategoryId() != null) {
            Category parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Parent category not found with id={}", request.getParentCategoryId());
                        return new CategoryNotFoundException(request.getParentCategoryId());
                    });
            category.setParentCategory(parent);
            logger.debug("Assigned parent category id={}", parent.getId());
        }

        Category saved = categoryRepository.save(category);
        logger.info("Category created successfully with id={}", saved.getId());
        return CategoryMapper.toResponse(saved);
    }

    @Override
    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        logger.debug("Fetching all categories, pageable={}", pageable);
        Page<CategoryResponse> page = categoryRepository.findAll(pageable)
                .map(CategoryMapper::toResponse);
        logger.debug("Fetched {} categories", page.getTotalElements());
        return page;
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        logger.info("Updating category id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with id={}", id);
                    return new CategoryNotFoundException(id);
                });

        Category parent = null;
        if (request.getParentCategoryId() != null) {
            if (request.getParentCategoryId().equals(id)) {
                logger.error("Attempted to set category id={} as its own parent", id);
                throw new IllegalArgumentException("A category cannot be its own parent.");
            }

            parent = categoryRepository.findById(request.getParentCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Parent category not found with id={}", request.getParentCategoryId());
                        return new CategoryNotFoundException(request.getParentCategoryId());
                    });

            logger.debug("Assigned new parent category id={}", parent.getId());
        }

        CategoryMapper.updateEntity(category, request, parent);
        Category saved = categoryRepository.save(category);

        logger.info("Category updated successfully id={}", saved.getId());
        return CategoryMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        logger.info("Deleting category id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Category not found with id={}", id);
                    return new CategoryNotFoundException(id);
                });

        if (!category.getProducts().isEmpty()) {
            logger.error("Cannot delete category id={} because it contains products", id);
            throw new CategoryDeletionNotAllowedException("Category contains products");
        }

        if (!category.getSubCategories().isEmpty()) {
            logger.error("Cannot delete category id={} because it contains subcategories", id);
            throw new CategoryDeletionNotAllowedException("Category contains subcategories");
        }

        categoryRepository.delete(category);
        logger.info("Category deleted successfully id={}", id);
    }
}
