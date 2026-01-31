package com.dimitrijevic175.warehouse_service.dto;

import com.dimitrijevic175.warehouse_service.domain.DispatchNoteStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DispatchNoteDto {
    private Long id;
    private Long salesOrderId;
    private Long warehouseId;
    private DispatchNoteStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
}
