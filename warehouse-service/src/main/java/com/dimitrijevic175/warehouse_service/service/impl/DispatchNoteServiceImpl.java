package com.dimitrijevic175.warehouse_service.service.impl;

import com.dimitrijevic175.warehouse_service.domain.DispatchNote;
import com.dimitrijevic175.warehouse_service.domain.DispatchNoteItem;
import com.dimitrijevic175.warehouse_service.domain.DispatchNoteStatus;
import com.dimitrijevic175.warehouse_service.domain.Warehouse;
import com.dimitrijevic175.warehouse_service.dto.CreateDispatchNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteResponseDto;
import com.dimitrijevic175.warehouse_service.mapper.DispatchNoteMapper;
import com.dimitrijevic175.warehouse_service.repository.DispatchNoteRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseRepository;
import com.dimitrijevic175.warehouse_service.service.DispatchNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchNoteServiceImpl implements DispatchNoteService {

    private final DispatchNoteRepository dispatchNoteRepository;
    private final WarehouseRepository warehouseRepository;

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

    @Override
    public DispatchNote createDispatchNote(CreateDispatchNoteRequestDto request) {

        // 1️⃣ Dohvati Warehouse entitet
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        // 2️⃣ Kreiraj DispatchNote entitet
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setSalesOrderId(request.getSalesOrderId());
        dispatchNote.setWarehouse(warehouse);
        dispatchNote.setStatus(DispatchNoteStatus.DRAFT);

        // 3️⃣ Kreiraj DispatchNoteItem entitete
        var items = request.getItems().stream().map(dtoItem -> {
            DispatchNoteItem item = new DispatchNoteItem();
            item.setDispatchNote(dispatchNote);
            item.setProductId(dtoItem.getProductId());
            item.setDispatchedQuantity(dtoItem.getQuantity());
            item.setSellingPrice(dtoItem.getSellingPrice());
            item.setDiscount(dtoItem.getDiscount());
            item.setTaxRate(new BigDecimal("20.00")); // fiksno 20%
            return item;
        }).toList();

        dispatchNote.setItems(items);

        // 4️⃣ Sačuvaj u bazi
        return dispatchNoteRepository.save(dispatchNote);
    }

}
