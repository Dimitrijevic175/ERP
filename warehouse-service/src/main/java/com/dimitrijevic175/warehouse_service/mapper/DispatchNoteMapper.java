package com.dimitrijevic175.warehouse_service.mapper;

import com.dimitrijevic175.warehouse_service.domain.DispatchNote;
import com.dimitrijevic175.warehouse_service.domain.DispatchNoteItem;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteResponseDto;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class DispatchNoteMapper {

    public static DispatchNoteResponseDto toDto(DispatchNote dispatchNote) {
        DispatchNoteResponseDto dto = new DispatchNoteResponseDto();
        dto.setId(dispatchNote.getId());
        dto.setSalesOrderId(dispatchNote.getSalesOrderId());
        dto.setWarehouseId(dispatchNote.getWarehouse().getId());
        dto.setStatus(dispatchNote.getStatus());
        dto.setCreatedAt(dispatchNote.getCreatedAt());
        dto.setConfirmedAt(dispatchNote.getConfirmedAt());

        dto.setItems(
                dispatchNote.getItems()
                        .stream()
                        .map(DispatchNoteMapper::toItemDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private static DispatchNoteResponseDto.DispatchNoteItemDto toItemDto(DispatchNoteItem item) {
        DispatchNoteResponseDto.DispatchNoteItemDto dto = new DispatchNoteResponseDto.DispatchNoteItemDto();
        dto.setId(item.getId());
        dto.setProductId(item.getProductId());
        dto.setDispatchedQuantity(item.getDispatchedQuantity());
        dto.setSalesPrice(item.getSellingPrice());
        return dto;
    }
}
