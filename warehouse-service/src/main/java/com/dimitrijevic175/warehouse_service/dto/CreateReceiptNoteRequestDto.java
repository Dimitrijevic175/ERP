package com.dimitrijevic175.warehouse_service.dto;

import lombok.Data;

@Data
public class CreateReceiptNoteRequestDto {
    private Long warehouseId;
    private Long purchaseOrderId;
}
