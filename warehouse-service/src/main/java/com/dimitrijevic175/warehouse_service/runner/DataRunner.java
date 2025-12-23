package com.dimitrijevic175.warehouse_service.runner;

import com.dimitrijevic175.warehouse_service.domain.*;
import com.dimitrijevic175.warehouse_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataRunner implements CommandLineRunner{

    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository stockRepository;
    private final ReceiptNoteRepository receiptNoteRepository;
    private final DispatchNoteRepository dispatchNoteRepository;

    @Override
    public void run(String... args) {

        // =========================
        // WAREHOUSE
        // =========================
        Warehouse warehouse = new Warehouse();
        warehouse.setName("Glavni Magacin Beograd");
        warehouse.setLocation("Bulevar Kralja Aleksandra 100");
        warehouseRepository.save(warehouse);

        // =========================
        // WAREHOUSE STOCK
        // =========================
        WarehouseStock stock1 = new WarehouseStock();
        stock1.setWarehouse(warehouse);
        stock1.setProductId(1001L);
        stock1.setQuantity(50);

        WarehouseStock stock2 = new WarehouseStock();
        stock2.setWarehouse(warehouse);
        stock2.setProductId(1002L);
        stock2.setQuantity(20);

        stockRepository.saveAll(List.of(stock1, stock2));

        // =========================
        // RECEIPT NOTE
        // =========================
        ReceiptNote receiptNote = new ReceiptNote();
        receiptNote.setPurchaseOrderId(1L);
        receiptNote.setWarehouse(warehouse);
        receiptNote.setStatus(ReceiptNoteStatus.DRAFT);

        ReceiptNoteItem rItem1 = new ReceiptNoteItem();
        rItem1.setReceiptNote(receiptNote);
        rItem1.setProductId(1001L);
        rItem1.setOrderedQuantity(10);
        rItem1.setReceivedQuantity(0);
        rItem1.setPurchasePrice(new BigDecimal("500.00"));

        ReceiptNoteItem rItem2 = new ReceiptNoteItem();
        rItem2.setReceiptNote(receiptNote);
        rItem2.setProductId(1002L);
        rItem2.setOrderedQuantity(5);
        rItem2.setReceivedQuantity(0);
        rItem2.setPurchasePrice(new BigDecimal("1500.00"));

        receiptNote.setItems(List.of(rItem1, rItem2));

        receiptNoteRepository.save(receiptNote);

        // =========================
        // DISPATCH NOTE
        // =========================
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setSalesOrderId(1L);
        dispatchNote.setWarehouse(warehouse);
        dispatchNote.setStatus(DispatchNoteStatus.DRAFT);

        DispatchNoteItem dItem1 = new DispatchNoteItem();
        dItem1.setDispatchNote(dispatchNote);
        dItem1.setProductId(1001L);
        dItem1.setDispatchedQuantity(5);
        dItem1.setSellingPrice(new BigDecimal("600.00"));
        dItem1.setDiscount(new BigDecimal("10.00"));
        dItem1.setTaxRate(new BigDecimal("20.00"));

        DispatchNoteItem dItem2 = new DispatchNoteItem();
        dItem2.setDispatchNote(dispatchNote);
        dItem2.setProductId(1002L);
        dItem2.setDispatchedQuantity(2);
        dItem2.setSellingPrice(new BigDecimal("1800.00"));
        dItem2.setDiscount(BigDecimal.ZERO);
        dItem2.setTaxRate(new BigDecimal("20.00"));

        dispatchNote.setItems(List.of(dItem1, dItem2));

        dispatchNoteRepository.save(dispatchNote);
    }

}
