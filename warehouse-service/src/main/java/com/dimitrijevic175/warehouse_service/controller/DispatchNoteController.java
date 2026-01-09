package com.dimitrijevic175.warehouse_service.controller;

import com.dimitrijevic175.warehouse_service.dto.DispatchNoteResponseDto;
import com.dimitrijevic175.warehouse_service.service.DispatchNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
