package com.dimitrijevic175.warehouse_service.service.impl;

import com.dimitrijevic175.warehouse_service.domain.*;
import com.dimitrijevic175.warehouse_service.dto.*;
import com.dimitrijevic175.warehouse_service.mapper.ReceiptNoteMapper;
import com.dimitrijevic175.warehouse_service.repository.ReceiptNoteRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseStockRepository;
import com.dimitrijevic175.warehouse_service.service.ReceiptNoteService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReceiptNoteServiceImpl implements ReceiptNoteService {

    private final ReceiptNoteRepository receiptNoteRepository;
    private final WarehouseRepository warehouseRepository;
    private final WebClient procurementWebClient;
    private final WarehouseStockRepository warehouseStockRepository;
    private final ReceiptNoteMapper receiptNoteMapper;
    private static final Logger log = LogManager.getLogger(ReceiptNoteServiceImpl.class);


    @Override
    public Page<ReceiptNoteResponseDto> getAllReceiptNotes(Pageable pageable) {
        log.info("Fetching all receipt notes, pageable={}", pageable);
        Page<ReceiptNoteResponseDto> page = receiptNoteRepository.findAll(pageable)
                .map(ReceiptNoteMapper::toDto);
        log.debug("Fetched {} receipt notes", page.getNumberOfElements());
        return page;
    }

    @Override
    public ReceiptNoteResponseDto getReceiptNoteById(Long id) {
        log.info("Fetching receipt note by id={}", id);
        ReceiptNote receiptNote = receiptNoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Receipt note not found with id={}", id);
                    return new RuntimeException("Receipt note not found");
                });
        ReceiptNoteResponseDto dto = ReceiptNoteMapper.toDto(receiptNote);
        log.debug("Fetched receipt note: {}", dto);
        return dto;
    }

    @Override
    public void deleteReceiptNote(Long id) {
        log.info("Deleting receipt note with id={}", id);
        ReceiptNote receiptNote = receiptNoteRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Receipt note not found with id={}", id);
                    return new RuntimeException("Receipt note not found");
                });
        receiptNoteRepository.delete(receiptNote);
        log.info("Receipt note with id={} deleted successfully", id);
    }

    @Override
    public ReceiptNoteResponseDto createReceiptNoteFromPurchaseOrder(CreateReceiptNoteRequestDto request) {
        log.info("Creating receipt note from purchase order id={} for warehouseId={}",
                request.getPurchaseOrderId(), request.getWarehouseId());

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> {
                    log.error("Warehouse not found with id={}", request.getWarehouseId());
                    return new RuntimeException("Warehouse not found");
                });

        PurchaseOrderDto purchaseOrder = procurementWebClient
                .get()
                .uri("/purchase-orders/{id}", request.getPurchaseOrderId())
                .retrieve()
                .bodyToMono(PurchaseOrderDto.class)
                .block();

        if (purchaseOrder == null) {
            log.error("Purchase order not found with id={}", request.getPurchaseOrderId());
            throw new RuntimeException("Purchase order not found");
        }

        ReceiptNote receiptNote = new ReceiptNote();
        receiptNote.setWarehouse(warehouse);
        receiptNote.setPurchaseOrderId(purchaseOrder.getId());
        receiptNote.setStatus(ReceiptNoteStatus.DRAFT);

        purchaseOrder.getItems().forEach(poItem -> {
            ReceiptNoteItem item = new ReceiptNoteItem();
            item.setReceiptNote(receiptNote);
            item.setProductId(poItem.getProductId());
            item.setOrderedQuantity(poItem.getQuantity());
            item.setReceivedQuantity(poItem.getQuantity()); // default
            item.setPurchasePrice(poItem.getPurchasePrice());
            receiptNote.getItems().add(item);
        });

        ReceiptNote saved = receiptNoteRepository.save(receiptNote);
        log.info("Receipt note created with id={} from purchase order id={}", saved.getId(), purchaseOrder.getId());

        return ReceiptNoteMapper.toDto(saved);
    }

    @Override
    public void confirmReceiptNote(Long receiptNoteId) {
        log.info("Confirming receipt note with id={}", receiptNoteId);

        ReceiptNote receiptNote = receiptNoteRepository.findById(receiptNoteId)
                .orElseThrow(() -> {
                    log.error("Receipt note not found with id={}", receiptNoteId);
                    return new RuntimeException("Receipt note not found");
                });

        if (receiptNote.getStatus() == ReceiptNoteStatus.CONFIRMED) {
            log.warn("Receipt note with id={} already confirmed", receiptNoteId);
            throw new IllegalStateException("Receipt note already confirmed");
        }

        Warehouse warehouse = receiptNote.getWarehouse();

        receiptNote.getItems().forEach(item -> {
            WarehouseStock stock = warehouseStockRepository
                    .findByWarehouseIdAndProductId(warehouse.getId(), item.getProductId())
                    .orElseGet(() -> {
                        WarehouseStock ws = new WarehouseStock();
                        ws.setWarehouse(warehouse);
                        ws.setProductId(item.getProductId());
                        ws.setQuantity(0);
                        return ws;
                    });

            log.debug("Updating stock for productId={} in warehouseId={} (currentQty={}, addQty={})",
                    item.getProductId(), warehouse.getId(), stock.getQuantity(), item.getReceivedQuantity());

            stock.setQuantity(stock.getQuantity() + item.getReceivedQuantity());
            warehouseStockRepository.save(stock);
        });

        receiptNote.setStatus(ReceiptNoteStatus.CONFIRMED);
        receiptNote.setConfirmedAt(LocalDateTime.now());
        receiptNoteRepository.save(receiptNote);

        log.info("Receipt note with id={} confirmed successfully", receiptNoteId);

        try {
            String response = procurementWebClient
                    .get()
                    .uri("/purchase-orders/{id}/close", receiptNote.getPurchaseOrderId())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Purchase order id={} closed successfully: {}", receiptNote.getPurchaseOrderId(), response);
        } catch (Exception e) {
            log.error("Failed to close purchase order with id={}", receiptNote.getPurchaseOrderId(), e);
        }
    }

}
