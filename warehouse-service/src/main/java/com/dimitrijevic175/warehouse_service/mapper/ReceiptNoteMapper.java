package com.dimitrijevic175.warehouse_service.mapper;

import com.dimitrijevic175.warehouse_service.domain.ReceiptNote;
import com.dimitrijevic175.warehouse_service.domain.ReceiptNoteItem;
import com.dimitrijevic175.warehouse_service.dto.ReceiptNoteResponseDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
@Component
public class ReceiptNoteMapper {

    private ReceiptNoteMapper() {
    }

    public static ReceiptNoteResponseDto toDto(ReceiptNote receiptNote) {
        ReceiptNoteResponseDto dto = new ReceiptNoteResponseDto();
        dto.setId(receiptNote.getId());
        dto.setPurchaseOrderId(receiptNote.getPurchaseOrderId());
        dto.setWarehouseId(receiptNote.getWarehouse().getId());
        dto.setStatus(receiptNote.getStatus());
        dto.setCreatedAt(receiptNote.getCreatedAt());

        dto.setItems(
                receiptNote.getItems()
                        .stream()
                        .map(ReceiptNoteMapper::toItemDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private static ReceiptNoteResponseDto.ReceiptNoteItemDto toItemDto(
            ReceiptNoteItem item
    ) {
        ReceiptNoteResponseDto.ReceiptNoteItemDto dto =
                new ReceiptNoteResponseDto.ReceiptNoteItemDto();

        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setOrderedQuantity(item.getOrderedQuantity());
        dto.setReceivedQuantity(item.getReceivedQuantity());
        dto.setPurchasePrice(item.getPurchasePrice());

        return dto;
    }
}
