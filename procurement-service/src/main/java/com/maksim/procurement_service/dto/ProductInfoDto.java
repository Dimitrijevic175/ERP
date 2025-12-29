package com.maksim.procurement_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductInfoDto {
    private Long id;
    private String name;
    private BigDecimal purchasePrice;
    private Integer maxQuantity;
}
