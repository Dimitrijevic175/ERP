package com.dimitrijevic175.product_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {

    @NotBlank(message = "Name is required.")
    private String name;

    @NotBlank(message = "SKU is required.")
    private String sku;

    private String description;

    @NotNull(message = "Category ID is required.")
    private Long categoryId;

    private String brand;

    @NotNull(message = "Purchase price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Purchase price must be positive.")
    private BigDecimal purchasePrice;

    @NotNull(message = "Selling price is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Selling price must be positive.")
    private BigDecimal sellingPrice;

    private BigDecimal taxRate;

    @NotNull(message = "Quantity is required.")
    @Min(value = 0, message = "Quantity cannot be negative.")
    private Integer quantity;

    private Integer minQuantity;
    private Integer maxQuantity;

    @NotBlank(message = "Unit of measure is required.")
    private String unitOfMeasure;
}
