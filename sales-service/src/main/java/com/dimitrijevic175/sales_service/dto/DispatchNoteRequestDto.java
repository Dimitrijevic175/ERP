package com.dimitrijevic175.sales_service.dto;

import com.dimitrijevic175.sales_service.domain.SalesOrder;
import com.dimitrijevic175.sales_service.domain.SalesOrderItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class DispatchNoteRequestDto {

    private Long warehouseId;
    private Long salesOrderId;
    private List<DispatchNoteItemDto> items;

    public DispatchNoteRequestDto(SalesOrder salesOrder) {
        this.warehouseId = salesOrder.getWarehouseId();
        this.salesOrderId = salesOrder.getId();
        this.items = salesOrder.getItems().stream()
                .map(DispatchNoteItemDto::new)
                .collect(Collectors.toList());
    }

    @Data
    public static class DispatchNoteItemDto {
        private Long productId;
        private Integer quantity;
        private BigDecimal sellingPrice;

        public DispatchNoteItemDto(SalesOrderItem item) {
            this.productId = item.getProductId();
            this.quantity = item.getQuantity();
            this.sellingPrice = item.getSellingPrice();
        }
    }
}
