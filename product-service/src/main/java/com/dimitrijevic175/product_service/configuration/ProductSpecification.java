package com.dimitrijevic175.product_service.configuration;

import com.dimitrijevic175.product_service.domain.Product;
import com.dimitrijevic175.product_service.dto.ProductSearchRequest;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ProductSpecification {

    public static Specification<Product> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Product> hasSku(String sku) {
        return (root, query, cb) ->
                sku == null ? null :
                        cb.like(cb.lower(root.get("sku")), "%" + sku.toLowerCase() + "%");
    }

    public static Specification<Product> inCategory(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null :
                        cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Product> hasBrand(String brand) {
        return (root, query, cb) ->
                brand == null ? null :
                        cb.like(cb.lower(root.get("brand")), "%" + brand.toLowerCase() + "%");
    }

    public static Specification<Product> minPrice(BigDecimal minPrice) {
        return (root, query, cb) ->
                minPrice == null ? null :
                        cb.greaterThanOrEqualTo(root.get("sellingPrice"), minPrice);
    }

    public static Specification<Product> maxPrice(BigDecimal maxPrice) {
        return (root, query, cb) ->
                maxPrice == null ? null :
                        cb.lessThanOrEqualTo(root.get("sellingPrice"), maxPrice);
    }

    public static Specification<Product> isActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? null :
                        cb.equal(root.get("active"), active);
    }
}
