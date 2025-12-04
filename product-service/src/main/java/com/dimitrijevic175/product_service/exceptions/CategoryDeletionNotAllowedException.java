package com.dimitrijevic175.product_service.exceptions;

public class CategoryDeletionNotAllowedException extends RuntimeException {
    public CategoryDeletionNotAllowedException(String reason) {
        super("Category cannot be deleted: " + reason);
    }
}
