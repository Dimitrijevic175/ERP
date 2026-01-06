package com.dimitrijevic175.warehouse_service.service;

import com.dimitrijevic175.warehouse_service.dto.CreateReceiptNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.ReceiptNoteResponseDto;

public interface ReceiptNoteService {

    ReceiptNoteResponseDto createReceiptNoteFromPurchaseOrder(CreateReceiptNoteRequestDto request);
    void confirmReceiptNote(Long receiptNoteId);
}
