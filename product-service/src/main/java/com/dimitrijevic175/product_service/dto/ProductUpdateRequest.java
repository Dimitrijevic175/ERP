package com.dimitrijevic175.product_service.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductUpdateRequest {

    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    @Size(min = 2, max = 50, message = "SKU must be between 2 and 50 characters")
    private String sku;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    private Long categoryId;

    @Size(max = 100, message = "Brand must be at most 100 characters")
    private String brand;

    @DecimalMin(value = "0.0", inclusive = true, message = "Purchase price must be non-negative")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Selling price must be non-negative")
    private BigDecimal sellingPrice;

    @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be non-negative")
    private BigDecimal taxRate;

    @Min(value = 0, message = "Quantity must be non-negative")
    private Integer quantity;

    @Min(value = 0, message = "Min quantity must be non-negative")
    private Integer minQuantity;

    @Min(value = 0, message = "Max quantity must be non-negative")
    private Integer maxQuantity;

    @Size(max = 50, message = "Unit of measure must be at most 50 characters")
    private String unitOfMeasure;
    private Boolean active;
}
