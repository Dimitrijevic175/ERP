package com.dimitrijevic175.warehouse_service.controller;

import com.dimitrijevic175.warehouse_service.dto.CreateReceiptNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.ReceiptNoteResponseDto;
import com.dimitrijevic175.warehouse_service.service.ReceiptNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/receipt-notes")
public class ReceiptNoteController {

    private final ReceiptNoteService receiptNoteService;

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
