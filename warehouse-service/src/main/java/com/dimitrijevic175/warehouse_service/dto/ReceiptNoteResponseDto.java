package com.dimitrijevic175.warehouse_service.dto;
import com.dimitrijevic175.warehouse_service.domain.ReceiptNoteStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReceiptNoteResponseDto {

    private Long id;
    private Long purchaseOrderId;
    private Long warehouseId;
    private ReceiptNoteStatus status;
    private LocalDateTime createdAt;

    private List<ReceiptNoteItemDto> items;

    @Data
    public static class ReceiptNoteItemDto {
        private Long id;
        private Long productId;
        private Integer orderedQuantity;
        private Integer receivedQuantity;
        private BigDecimal purchasePrice;
    }
}
