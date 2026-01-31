package com.dimitrijevic175.warehouse_service.service.impl;

import com.dimitrijevic175.warehouse_service.domain.*;
import com.dimitrijevic175.warehouse_service.dto.CreateDispatchNoteRequestDto;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteDto;
import com.dimitrijevic175.warehouse_service.dto.DispatchNoteResponseDto;
import com.dimitrijevic175.warehouse_service.mapper.DispatchNoteMapper;
import com.dimitrijevic175.warehouse_service.repository.DispatchNoteRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseStockRepository;
import com.dimitrijevic175.warehouse_service.service.DispatchNoteService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchNoteServiceImpl implements DispatchNoteService {

    private final DispatchNoteRepository dispatchNoteRepository;
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private static final Logger log = LogManager.getLogger(DispatchNoteServiceImpl.class);

    @Override
    public Page<DispatchNoteResponseDto> getAllDispatchNotes(Pageable pageable) {
        log.info("Fetching all dispatch notes, pageable={}", pageable);
        Page<DispatchNoteResponseDto> page = dispatchNoteRepository.findAll(pageable)
                .map(DispatchNoteMapper::toDto);
        log.debug("Fetched {} dispatch notes", page.getNumberOfElements());
        return page;
    }

    @Override
    public DispatchNoteResponseDto getDispatchNoteById(Long id) {
        log.info("Fetching dispatch note by id={}", id);
        DispatchNote dispatchNote = dispatchNoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Dispatch note not found with id={}", id);
                    return new RuntimeException("Dispatch note not found");
                });
        DispatchNoteResponseDto dto = DispatchNoteMapper.toDto(dispatchNote);
        log.debug("Fetched dispatch note: {}", dto);
        return dto;
    }

    @Override
    public void deleteDispatchNote(Long id) {
        log.info("Deleting dispatch note with id={}", id);
        DispatchNote dispatchNote = dispatchNoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Dispatch note not found with id={}", id);
                    return new RuntimeException("Dispatch note not found");
                });
        dispatchNoteRepository.delete(dispatchNote);
        log.info("Dispatch note with id={} deleted successfully", id);
    }

    @Override
    public DispatchNote createDispatchNote(CreateDispatchNoteRequestDto request) {
        log.info("Creating dispatch note for salesOrderId={} in warehouseId={}",
                request.getSalesOrderId(), request.getWarehouseId());

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> {
                    log.error("Warehouse not found with id={}", request.getWarehouseId());
                    return new RuntimeException("Warehouse not found");
                });

        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setSalesOrderId(request.getSalesOrderId());
        dispatchNote.setWarehouse(warehouse);
        dispatchNote.setStatus(DispatchNoteStatus.DRAFT);

        var items = request.getItems().stream().map(dtoItem -> {
            DispatchNoteItem item = new DispatchNoteItem();
            item.setDispatchNote(dispatchNote);
            item.setProductId(dtoItem.getProductId());
            item.setDispatchedQuantity(dtoItem.getQuantity());
            item.setSellingPrice(dtoItem.getSellingPrice());
            item.setDiscount(dtoItem.getDiscount());
            item.setTaxRate(new BigDecimal("20.00")); // fiksno 20%
            log.debug("Added item to dispatch note: {}", item);
            return item;
        }).toList();

        dispatchNote.setItems(items);

        DispatchNote saved = dispatchNoteRepository.save(dispatchNote);
        log.info("Dispatch note created with id={} for salesOrderId={}", saved.getId(), request.getSalesOrderId());

        return saved;
    }

    @Override
    public DispatchNoteDto getDispatchNoteBySalesOrderId(Long salesOrderId) {
        log.info("Fetching dispatch note for salesOrderId={}", salesOrderId);

        DispatchNote note = dispatchNoteRepository.findBySalesOrderId(salesOrderId)
                .orElseThrow(() -> {
                    log.error("DispatchNote not found for salesOrderId={}", salesOrderId);
                    return new RuntimeException(
                            "DispatchNote not found for salesOrderId: " + salesOrderId
                    );
                });

        DispatchNoteDto dto = new DispatchNoteDto();
        dto.setId(note.getId());
        dto.setSalesOrderId(note.getSalesOrderId());
        dto.setWarehouseId(note.getWarehouse().getId());
        dto.setStatus(note.getStatus());
        dto.setCreatedAt(note.getCreatedAt());
        dto.setConfirmedAt(note.getConfirmedAt());

        log.debug("Fetched dispatch note DTO: {}", dto);
        return dto;
    }

    @Override
    public void rollbackDispatchNote(Long dispatchNoteId) {
        log.info("Rolling back dispatch note with id={}", dispatchNoteId);

        DispatchNote dispatchNote = dispatchNoteRepository.findById(dispatchNoteId)
                .orElseThrow(() -> {
                    log.error("Dispatch note not found with id={}", dispatchNoteId);
                    return new RuntimeException("Dispatch note not found");
                });

        if (dispatchNote.getStatus() != DispatchNoteStatus.DRAFT) {
            log.warn("Cannot rollback dispatch note with id={} because status={}", dispatchNoteId, dispatchNote.getStatus());
            throw new IllegalStateException(
                    "Cannot rollback dispatch note with status " + dispatchNote.getStatus()
            );
        }

        Warehouse warehouse = dispatchNote.getWarehouse();

        dispatchNote.getItems().forEach(item -> {
            WarehouseStock stock = warehouseStockRepository
                    .findByWarehouseIdAndProductId(warehouse.getId(), item.getProductId())
                    .orElseGet(() -> {
                        WarehouseStock ws = new WarehouseStock();
                        ws.setWarehouse(warehouse);
                        ws.setProductId(item.getProductId());
                        ws.setQuantity(0);
                        return ws;
                    });

            log.debug("Returning stock for productId={} in warehouseId={} (currentQty={}, addQty={})",
                    item.getProductId(), warehouse.getId(), stock.getQuantity(), item.getDispatchedQuantity());

            stock.setQuantity(stock.getQuantity() + item.getDispatchedQuantity());
            warehouseStockRepository.save(stock);
        });

        dispatchNoteRepository.delete(dispatchNote);
        log.info("Dispatch note with id={} rolled back successfully", dispatchNoteId);
    }

    @Override
    public void confirmDispatchNote(Long dispatchNoteId) {
        log.info("Confirming dispatch note with id={}", dispatchNoteId);

        DispatchNote dispatchNote = dispatchNoteRepository.findById(dispatchNoteId)
                .orElseThrow(() -> {
                    log.error("Dispatch note not found with id={}", dispatchNoteId);
                    return new RuntimeException("Dispatch note not found");
                });

        if (dispatchNote.getStatus() == DispatchNoteStatus.CONFIRMED) {
            log.warn("Dispatch note with id={} already confirmed", dispatchNoteId);
            throw new IllegalStateException("Dispatch note already confirmed");
        }

        Warehouse warehouse = dispatchNote.getWarehouse();

        dispatchNote.getItems().forEach(item -> {
            WarehouseStock stock = warehouseStockRepository
                    .findByWarehouseIdAndProductId(warehouse.getId(), item.getProductId())
                    .orElseThrow(() -> {
                        log.error("No stock found for productId={} in warehouseId={}", item.getProductId(), warehouse.getId());
                        return new RuntimeException(
                                "No stock for product " + item.getProductId()
                        );
                    });

            if (stock.getQuantity() < item.getDispatchedQuantity()) {
                log.error("Not enough stock for productId={} in warehouseId={} (currentQty={}, requiredQty={})",
                        item.getProductId(), warehouse.getId(), stock.getQuantity(), item.getDispatchedQuantity());
                throw new RuntimeException(
                        "Not enough stock for product " + item.getProductId()
                );
            }

            log.debug("Reducing stock for productId={} in warehouseId={} (currentQty={}, reduceQty={})",
                    item.getProductId(), warehouse.getId(), stock.getQuantity(), item.getDispatchedQuantity());

            stock.setQuantity(stock.getQuantity() - item.getDispatchedQuantity());
            warehouseStockRepository.save(stock);
        });

        dispatchNote.setStatus(DispatchNoteStatus.CONFIRMED);
        dispatchNote.setConfirmedAt(LocalDateTime.now());
        dispatchNoteRepository.save(dispatchNote);

        log.info("Dispatch note with id={} confirmed successfully", dispatchNoteId);
    }

}
