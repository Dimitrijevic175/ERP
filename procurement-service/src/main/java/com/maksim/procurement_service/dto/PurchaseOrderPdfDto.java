package com.maksim.procurement_service.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class PurchaseOrderPdfDto {
    private String warehouseName;
    private String warehouseLocation;
    private String supplierName;
    private String supplierFullName;
    private String supplierEmail;
    private String supplierPhone;
    private LocalDateTime submittedAt;
    private List<PurchaseOrderPdfItemDto> items = new ArrayList<>();
}
