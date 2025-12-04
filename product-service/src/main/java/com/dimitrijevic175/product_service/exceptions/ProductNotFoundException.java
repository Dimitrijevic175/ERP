package com.dimitrijevic175.product_service.exceptions;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
