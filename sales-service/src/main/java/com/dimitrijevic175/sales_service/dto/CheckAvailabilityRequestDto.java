package com.dimitrijevic175.sales_service.dto;

import lombok.Data;

import java.util.List;

@Data
public class CheckAvailabilityRequestDto {
    private List<Item> items;

    @Data
    public static class Item {
        private Long productId;
        private Integer quantity;

        public Item(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
    }
}
