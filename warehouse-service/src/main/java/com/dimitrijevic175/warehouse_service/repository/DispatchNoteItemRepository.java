package com.dimitrijevic175.warehouse_service.repository;

import com.dimitrijevic175.warehouse_service.domain.DispatchNoteItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DispatchNoteItemRepository extends JpaRepository<DispatchNoteItem, Long> {

    List<DispatchNoteItem> findByDispatchNoteId(Long dispatchNoteId);
}
