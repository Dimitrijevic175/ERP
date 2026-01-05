package com.maksim.procurement_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private String unitOfMeasure;
    private BigDecimal purchasePrice;
    private Integer maxQuantity; // maksimalna količina za PO
}
