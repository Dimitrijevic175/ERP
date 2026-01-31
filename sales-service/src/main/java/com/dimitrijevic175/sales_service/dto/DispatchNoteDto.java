package com.dimitrijevic175.sales_service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DispatchNoteDto {
    private Long id;
    private Long salesOrderId;
    private Long warehouseId;
    private DispatchNoteStatusDto status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}
