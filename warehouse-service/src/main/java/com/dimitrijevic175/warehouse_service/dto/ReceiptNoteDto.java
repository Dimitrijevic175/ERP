package com.dimitrijevic175.warehouse_service.dto;

import com.dimitrijevic175.warehouse_service.domain.ReceiptNoteStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReceiptNoteDto {
    private Long id;
    private Long purchaseOrderId;
    private Long warehouseId;
    private ReceiptNoteStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}
