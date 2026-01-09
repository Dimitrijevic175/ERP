package com.dimitrijevic175.warehouse_service.service.impl;

import com.dimitrijevic175.warehouse_service.domain.*;
import com.dimitrijevic175.warehouse_service.dto.*;
import com.dimitrijevic175.warehouse_service.mapper.ReceiptNoteMapper;
import com.dimitrijevic175.warehouse_service.repository.ReceiptNoteRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseRepository;
import com.dimitrijevic175.warehouse_service.repository.WarehouseStockRepository;
import com.dimitrijevic175.warehouse_service.service.ReceiptNoteService;
import lombok.RequiredArgsConstructor;
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


    @Override
    public Page<ReceiptNoteResponseDto> getAllReceiptNotes(Pageable pageable) {
        return receiptNoteRepository.findAll(pageable)
                .map(ReceiptNoteMapper::toDto);
    }

    @Override
    public ReceiptNoteResponseDto getReceiptNoteById(Long id) {
        ReceiptNote receiptNote = receiptNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt note not found"));
        return ReceiptNoteMapper.toDto(receiptNote);
    }

    @Override
    public void deleteReceiptNote(Long id) {
        ReceiptNote receiptNote = receiptNoteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt note not found"));
        receiptNoteRepository.delete(receiptNote);
    }

    @Override
    public ReceiptNoteResponseDto createReceiptNoteFromPurchaseOrder(
            CreateReceiptNoteRequestDto request
    ) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        PurchaseOrderDto purchaseOrder = procurementWebClient
                .get()
                .uri("/purchase-orders/{id}", request.getPurchaseOrderId())
                .retrieve()
                .bodyToMono(PurchaseOrderDto.class)
                .block();

        if (purchaseOrder == null) {
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

        return ReceiptNoteMapper.toDto(saved);
    }

    @Override
    public void confirmReceiptNote(Long receiptNoteId) {
        ReceiptNote receiptNote = receiptNoteRepository.findById(receiptNoteId)
                .orElseThrow(() -> new RuntimeException("Receipt note not found"));

        if (receiptNote.getStatus() == ReceiptNoteStatus.CONFIRMED) {
            throw new IllegalStateException("Receipt note already confirmed");
        }

        Warehouse warehouse = receiptNote.getWarehouse();

        receiptNote.getItems().forEach(item -> {
            WarehouseStock stock = warehouseStockRepository
                    .findByWarehouseIdAndProductId(
                            warehouse.getId(),
                            item.getProductId()
                    )
                    .orElseGet(() -> {
                        WarehouseStock ws = new WarehouseStock();
                        ws.setWarehouse(warehouse);
                        ws.setProductId(item.getProductId());
                        ws.setQuantity(item.getReceivedQuantity());
                        return ws;
                    });

            stock.setQuantity(
                    stock.getQuantity() + item.getReceivedQuantity()
            );

            warehouseStockRepository.save(stock);
        });

        receiptNote.setStatus(ReceiptNoteStatus.CONFIRMED);
        receiptNote.setConfirmedAt(LocalDateTime.now());

        receiptNoteRepository.save(receiptNote);

        try {
            String response = procurementWebClient
                    .get()
                    .uri("/purchase-orders/{id}/close", receiptNote.getPurchaseOrderId())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            System.out.println("Calling: /purchase-orders/" + receiptNote.getPurchaseOrderId() + "/close");

            System.out.println("Purchase order closed: " + response);
        } catch (Exception e) {
            System.err.println("Failed to close purchase order with id " + receiptNote.getPurchaseOrderId() + ": " + e.getMessage());
        }

    }

}
