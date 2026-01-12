package com.dimitrijevic175.warehouse_service.controller;

import com.dimitrijevic175.warehouse_service.domain.DispatchNote;
import com.dimitrijevic175.warehouse_service.dto.CreateDispatchNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteResponseDto;
import com.dimitrijevic175.warehouse_service.service.DispatchNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dispatch-notes")
@RequiredArgsConstructor
public class DispatchNoteController {

    private final DispatchNoteService dispatchNoteService;

    @GetMapping
    public ResponseEntity<Page<DispatchNoteResponseDto>> getAllDispatchNotes(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(dispatchNoteService.getAllDispatchNotes(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DispatchNoteResponseDto> getDispatchNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(dispatchNoteService.getDispatchNoteById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispatchNote(@PathVariable Long id) {
        dispatchNoteService.deleteDispatchNote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<DispatchNoteResponseDto> createDispatchNote(
            @RequestBody CreateDispatchNoteRequestDto request
    ) {
        DispatchNote dispatchNote = dispatchNoteService.createDispatchNote(request);

        DispatchNoteResponseDto response = new DispatchNoteResponseDto();
        response.setId(dispatchNote.getId());
        response.setSalesOrderId(dispatchNote.getSalesOrderId());
        response.setWarehouseId(dispatchNote.getWarehouse().getId());
        response.setStatus(dispatchNote.getStatus());
        response.setCreatedAt(dispatchNote.getCreatedAt());
        response.setConfirmedAt(dispatchNote.getConfirmedAt());

        List<DispatchNoteResponseDto.DispatchNoteItemDto> items = dispatchNote.getItems().stream().map(item -> {
            DispatchNoteResponseDto.DispatchNoteItemDto dtoItem = new DispatchNoteResponseDto.DispatchNoteItemDto();
            dtoItem.setId(item.getId());
            dtoItem.setProductId(item.getProductId());
            dtoItem.setDispatchedQuantity(item.getDispatchedQuantity());
            dtoItem.setSalesPrice(item.getSellingPrice());
            return dtoItem;
        }).toList();

        response.setItems(items);

        return ResponseEntity.ok(response);
    }
}
