package com.dimitrijevic175.warehouse_service.dto;

import com.dimitrijevic175.warehouse_service.domain.DispatchNoteStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class DispatchNoteResponseDto {
    private Long id;
    private Long salesOrderId;
    private Long warehouseId;
    private DispatchNoteStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private List<DispatchNoteItemDto> items;

    @Data
    public static class DispatchNoteItemDto {
        private Long id;
        private Long productId;
        private Integer dispatchedQuantity;
        private java.math.BigDecimal salesPrice;
    }


}
