package com.maksim.procurement_service.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseOrderPdfItemDto {
    private String productName;
    private String brand;
    private String unitOfMeasure;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal total;
}
