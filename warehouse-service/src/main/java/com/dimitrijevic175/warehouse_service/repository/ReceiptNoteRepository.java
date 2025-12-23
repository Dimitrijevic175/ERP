package com.dimitrijevic175.warehouse_service.repository;

import com.dimitrijevic175.warehouse_service.domain.ReceiptNote;
import com.dimitrijevic175.warehouse_service.domain.ReceiptNoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiptNoteRepository extends JpaRepository<ReceiptNote, Long> {

    List<ReceiptNote> findByWarehouseId(Long warehouseId);

    List<ReceiptNote> findByStatus(ReceiptNoteStatus status);

    List<ReceiptNote> findByPurchaseOrderId(Long purchaseOrderId);
}


