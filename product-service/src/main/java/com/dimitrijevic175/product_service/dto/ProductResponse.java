package com.dimitrijevic175.product_service.dto;

import lombok.Builder;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String sku;
    private String description;
    private Long categoryId;
    private String brand;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal taxRate;
    private Integer quantity;
    private Integer minQuantity;
    private Integer maxQuantity;
    private String unitOfMeasure;
    private Boolean active;

}
