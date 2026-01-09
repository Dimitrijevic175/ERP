package com.dimitrijevic175.warehouse_service.service.impl;

import com.dimitrijevic175.warehouse_service.domain.DispatchNote;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteResponseDto;
import com.dimitrijevic175.warehouse_service.mapper.DispatchNoteMapper;
import com.dimitrijevic175.warehouse_service.repository.DispatchNoteRepository;
import com.dimitrijevic175.warehouse_service.service.DispatchNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchNoteServiceImpl implements DispatchNoteService {

    private final DispatchNoteRepository dispatchNoteRepository;

    @Override
    public Page<DispatchNoteResponseDto> getAllDispatchNotes(Pageable pageable) {
        return dispatchNoteRepository.findAll(pageable)
                .map(DispatchNoteMapper::toDto);
    }

    @Override
    public DispatchNoteResponseDto getDispatchNoteById(Long id) {
        DispatchNote dispatchNote = dispatchNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispatch note not found"));
        return DispatchNoteMapper.toDto(dispatchNote);
    }

    @Override
    public void deleteDispatchNote(Long id) {
        DispatchNote dispatchNote = dispatchNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dispatch note not found"));
        dispatchNoteRepository.delete(dispatchNote);
    }
}
