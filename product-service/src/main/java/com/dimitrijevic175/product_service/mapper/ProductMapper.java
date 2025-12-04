package com.dimitrijevic175.product_service.mapper;

import com.dimitrijevic175.product_service.domain.Category;
import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.dto.ProductResponse;
import com.dimitrijevic175.product_service.dto.ProductUpdateRequest;

public class ProductMapper {

    public static ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .categoryId(product.getCategory().getId())
                .brand(product.getBrand())
                .purchasePrice(product.getPurchasePrice())
                .sellingPrice(product.getSellingPrice())
                .taxRate(product.getTaxRate())
                .quantity(product.getQuantity())
                .minQuantity(product.getMinQuantity())
                .maxQuantity(product.getMaxQuantity())
                .unitOfMeasure(product.getUnitOfMeasure())
                .active(product.getActive())
                .build();
    }

    public static void updateEntity(Product product, ProductUpdateRequest request, Category category) {
        if (request.getName() != null) product.setName(request.getName());
        if (request.getSku() != null) product.setSku(request.getSku());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (category != null) product.setCategory(category);
        if (request.getBrand() != null) product.setBrand(request.getBrand());
        if (request.getPurchasePrice() != null) product.setPurchasePrice(request.getPurchasePrice());
        if (request.getSellingPrice() != null) product.setSellingPrice(request.getSellingPrice());
        if (request.getTaxRate() != null) product.setTaxRate(request.getTaxRate());
        if (request.getQuantity() != null) product.setQuantity(request.getQuantity());
        if (request.getMinQuantity() != null) product.setMinQuantity(request.getMinQuantity());
        if (request.getMaxQuantity() != null) product.setMaxQuantity(request.getMaxQuantity());
        if (request.getUnitOfMeasure() != null) product.setUnitOfMeasure(request.getUnitOfMeasure());
        if (request.getActive() != null) product.setActive(request.getActive());
    }
}
