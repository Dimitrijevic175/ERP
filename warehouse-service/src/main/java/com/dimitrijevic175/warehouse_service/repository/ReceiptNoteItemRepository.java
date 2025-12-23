package com.dimitrijevic175.warehouse_service.repository;

import com.dimitrijevic175.warehouse_service.domain.ReceiptNoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptNoteItemRepository extends JpaRepository<ReceiptNoteItem, Long> {

    List<ReceiptNoteItem> findByReceiptNoteId(Long receiptNoteId);
}
