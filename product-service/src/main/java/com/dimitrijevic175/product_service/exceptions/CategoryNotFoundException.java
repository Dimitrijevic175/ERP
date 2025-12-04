package com.dimitrijevic175.product_service.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(Long id) {
        super("Category not found with id: " + id);
    }

}
