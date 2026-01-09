package com.dimitrijevic175.warehouse_service.controller;

import com.dimitrijevic175.warehouse_service.dto.CreateReceiptNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.ReceiptNoteDto;
import com.dimitrijevic175.warehouse_service.dto.ReceiptNoteResponseDto;
import com.dimitrijevic175.warehouse_service.service.ReceiptNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipt-notes")
public class ReceiptNoteController {

    private final ReceiptNoteService receiptNoteService;

    @GetMapping
    public ResponseEntity<Page<ReceiptNoteResponseDto>> getAllReceiptNotes(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                receiptNoteService.getAllReceiptNotes(pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceiptNoteResponseDto> getReceiptNoteById(@PathVariable Long id) {
        return ResponseEntity.ok(
                receiptNoteService.getReceiptNoteById(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceiptNote(@PathVariable Long id) {
        receiptNoteService.deleteReceiptNote(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<ReceiptNoteResponseDto> createReceiptNote(@RequestBody CreateReceiptNoteRequestDto request) {
        return ResponseEntity.ok(receiptNoteService.createReceiptNoteFromPurchaseOrder(request));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmReceiptNote(@PathVariable Long id) {
        receiptNoteService.confirmReceiptNote(id);
        return ResponseEntity.ok().build();
    }


}
