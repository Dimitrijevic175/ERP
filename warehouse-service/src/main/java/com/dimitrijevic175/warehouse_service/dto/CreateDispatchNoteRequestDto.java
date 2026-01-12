package com.dimitrijevic175.warehouse_service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateDispatchNoteRequestDto {

    private Long salesOrderId;
    private Long warehouseId;
    private List<Item> items;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;
        private BigDecimal sellingPrice;
        private BigDecimal discount;
    }


}
