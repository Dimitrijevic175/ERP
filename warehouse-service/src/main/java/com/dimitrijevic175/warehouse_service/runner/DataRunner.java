package com.dimitrijevic175.warehouse_service.runner;

import com.dimitrijevic175.warehouse_service.domain.*;
import com.dimitrijevic175.warehouse_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataRunner implements CommandLineRunner {

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
        // SECOND WAREHOUSE
        // =========================
        Warehouse warehouse2 = new Warehouse();
        warehouse2.setName("Pomoćni Magacin Novi Sad");
        warehouse2.setLocation("Industrijska zona bb, Novi Sad");
        warehouseRepository.save(warehouse2);

        // =========================
        // WAREHOUSE STOCK
        // =========================
        // Povezujemo sa Product ID iz product_service
        WarehouseStock stock1 = new WarehouseStock();
        stock1.setWarehouse(warehouse);
        stock1.setProductId(1L); // Portland cement
        stock1.setQuantity(9);

        WarehouseStock stock2 = new WarehouseStock();
        stock2.setWarehouse(warehouse);
        stock2.setProductId(2L); // Rapid cement
        stock2.setQuantity(50);

        WarehouseStock stock3 = new WarehouseStock();
        stock3.setWarehouse(warehouse);
        stock3.setProductId(3L); // Betonski pesak
        stock3.setQuantity(30);

        // =========================
        // WAREHOUSE 2 STOCK
        // =========================
        WarehouseStock stock4 = new WarehouseStock();
        stock4.setWarehouse(warehouse2);
        stock4.setProductId(1L); // Portland cement
        stock4.setQuantity(1);

        stockRepository.saveAll(List.of(stock1, stock2, stock3));
        stockRepository.save(stock4);
        // =========================
        // RECEIPT NOTE
        // =========================
        ReceiptNote receiptNote = new ReceiptNote();
        receiptNote.setPurchaseOrderId(101L);
        receiptNote.setWarehouse(warehouse);
        receiptNote.setStatus(ReceiptNoteStatus.DRAFT);

        ReceiptNoteItem rItem1 = new ReceiptNoteItem();
        rItem1.setReceiptNote(receiptNote);
        rItem1.setProductId(1L);
        rItem1.setOrderedQuantity(20);
        rItem1.setReceivedQuantity(0);
        rItem1.setPurchasePrice(new BigDecimal("4.50"));

        ReceiptNoteItem rItem2 = new ReceiptNoteItem();
        rItem2.setReceiptNote(receiptNote);
        rItem2.setProductId(2L);
        rItem2.setOrderedQuantity(10);
        rItem2.setReceivedQuantity(0);
        rItem2.setPurchasePrice(new BigDecimal("5.00"));

        receiptNote.setItems(List.of(rItem1, rItem2));

        receiptNoteRepository.save(receiptNote);

        // =========================
        // DISPATCH NOTE
        // =========================
        DispatchNote dispatchNote = new DispatchNote();
        dispatchNote.setSalesOrderId(201L);
        dispatchNote.setWarehouse(warehouse);
        dispatchNote.setStatus(DispatchNoteStatus.DRAFT);

        DispatchNoteItem dItem1 = new DispatchNoteItem();
        dItem1.setDispatchNote(dispatchNote);
        dItem1.setProductId(1L);
        dItem1.setDispatchedQuantity(10);
        dItem1.setSellingPrice(new BigDecimal("6.50"));
        dItem1.setDiscount(new BigDecimal("0.50"));
        dItem1.setTaxRate(new BigDecimal("20.00"));

        DispatchNoteItem dItem2 = new DispatchNoteItem();
        dItem2.setDispatchNote(dispatchNote);
        dItem2.setProductId(2L);
        dItem2.setDispatchedQuantity(5);
        dItem2.setSellingPrice(new BigDecimal("7.00"));
        dItem2.setDiscount(BigDecimal.ZERO);
        dItem2.setTaxRate(new BigDecimal("20.00"));

        dispatchNote.setItems(List.of(dItem1, dItem2));

        dispatchNoteRepository.save(dispatchNote);

        System.out.println("Warehouse initial data loaded successfully!");
    }
}
