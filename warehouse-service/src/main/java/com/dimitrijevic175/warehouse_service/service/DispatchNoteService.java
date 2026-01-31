package com.dimitrijevic175.warehouse_service.service;
import com.dimitrijevic175.warehouse_service.domain.DispatchNote;
import com.dimitrijevic175.warehouse_service.dto.CreateDispatchNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteDto;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface DispatchNoteService {
    Page<DispatchNoteResponseDto> getAllDispatchNotes(Pageable pageable);
    DispatchNoteResponseDto getDispatchNoteById(Long id);
    void deleteDispatchNote(Long id);
    DispatchNote createDispatchNote(CreateDispatchNoteRequestDto request);
    DispatchNoteDto getDispatchNoteBySalesOrderId(Long salesOrderId);
    void rollbackDispatchNote(Long dispatchNoteId);
    void confirmDispatchNote(Long dispatchNoteId);

}
