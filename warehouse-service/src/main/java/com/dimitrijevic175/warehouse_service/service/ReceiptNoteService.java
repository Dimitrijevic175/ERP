package com.dimitrijevic175.warehouse_service.service;

import com.dimitrijevic175.warehouse_service.dto.CreateReceiptNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.ReceiptNoteDto;
import com.dimitrijevic175.warehouse_service.dto.ReceiptNoteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReceiptNoteService {
    Page<ReceiptNoteResponseDto> getAllReceiptNotes(Pageable pageable);

    ReceiptNoteResponseDto getReceiptNoteById(Long id);
    void deleteReceiptNote(Long id);
    ReceiptNoteResponseDto createReceiptNoteFromPurchaseOrder(CreateReceiptNoteRequestDto request);
    void confirmReceiptNote(Long receiptNoteId);
}
