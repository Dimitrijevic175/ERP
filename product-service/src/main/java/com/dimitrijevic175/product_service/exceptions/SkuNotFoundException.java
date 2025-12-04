package com.dimitrijevic175.product_service.exceptions;

public class SkuNotFoundException extends RuntimeException {
    public SkuNotFoundException(String id) {
        super("SKU not found with id: " + id);
    }

}
