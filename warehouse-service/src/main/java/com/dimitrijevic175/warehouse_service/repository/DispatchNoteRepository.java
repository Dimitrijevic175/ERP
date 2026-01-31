package com.dimitrijevic175.warehouse_service.repository;

import com.dimitrijevic175.warehouse_service.domain.DispatchNote;
import com.dimitrijevic175.warehouse_service.domain.DispatchNoteStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchNoteRepository extends JpaRepository<DispatchNote, Long> {

    List<DispatchNote> findByWarehouseId(Long warehouseId);

    List<DispatchNote> findByStatus(DispatchNoteStatus status);

    Optional<DispatchNote> findBySalesOrderId(Long salesOrderId);

}
