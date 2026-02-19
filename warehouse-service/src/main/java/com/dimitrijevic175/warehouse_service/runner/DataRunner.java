package com.dimitrijevic175.warehouse_service.runner;

import com.dimitrijevic175.warehouse_service.domain.*;
import com.dimitrijevic175.warehouse_service.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        if (warehouseRepository.count() > 0) return;

        // =========================
        // WAREHOUSES
        // =========================
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setName("Glavni Magacin Beograd");
        warehouse1.setLocation("Bulevar Kralja Aleksandra 100");
        warehouseRepository.save(warehouse1);

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setName("Pomoćni Magacin Novi Sad");
        warehouse2.setLocation("Industrijska zona bb, Novi Sad");
        warehouseRepository.save(warehouse2);

        Warehouse warehouse3 = new Warehouse();
        warehouse3.setName("Magacin Kragujevac");
        warehouse3.setLocation("Industrijska 5, Kragujevac");
        warehouseRepository.save(warehouse3);

        // =========================
        // WAREHOUSE STOCK - po 10 proizvoda po magacinu
        // =========================
        List<WarehouseStock> allStock = new ArrayList<>();

        for (long productId = 1; productId <= 10; productId++) {
            WarehouseStock ws1 = new WarehouseStock();
            ws1.setWarehouse(warehouse1);
            ws1.setProductId(productId);
            ws1.setQuantity(50 + (int)(Math.random()*50));
            allStock.add(ws1);

            WarehouseStock ws2 = new WarehouseStock();
            ws2.setWarehouse(warehouse2);
            ws2.setProductId(productId);
            ws2.setQuantity(30 + (int)(Math.random()*40));
            allStock.add(ws2);

            WarehouseStock ws3 = new WarehouseStock();
            ws3.setWarehouse(warehouse3);
            ws3.setProductId(productId);
            ws3.setQuantity(20 + (int)(Math.random()*30));
            allStock.add(ws3);
        }

        stockRepository.saveAll(allStock);

        // =========================
        // RECEIPT NOTES - povezuju se sa stvarnim PurchaseOrder ID-jevima
        // =========================
        long[] poIds = {1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L};

        for (int i = 0; i < poIds.length; i++) {
            ReceiptNote rn = new ReceiptNote();
            rn.setPurchaseOrderId(poIds[i]);
            rn.setWarehouse(i % 3 == 0 ? warehouse1 : (i % 3 == 1 ? warehouse2 : warehouse3));
            rn.setStatus(ReceiptNoteStatus.DRAFT);

            List<ReceiptNoteItem> items = new ArrayList<>();
            for (long productId = 1; productId <= 3; productId++) { // svaka porudžbina ima 3 proizvoda
                ReceiptNoteItem item = new ReceiptNoteItem();
                item.setReceiptNote(rn);
                item.setProductId(productId);
                item.setOrderedQuantity(10 + (int)(Math.random()*20));
                item.setReceivedQuantity(0);
                item.setPurchasePrice(BigDecimal.valueOf(5 + Math.random()*5));
                items.add(item);
            }
            rn.setItems(items);
            receiptNoteRepository.save(rn);
        }

        // =========================
        // DISPATCH NOTES - demo sa random proizvodima
        // =========================
        for (long soId = 201; soId <= 212; soId++) { // salesOrder IDs iz sales_service
            DispatchNote dn = new DispatchNote();
            dn.setSalesOrderId(soId);
            dn.setWarehouse(soId % 3 == 0 ? warehouse1 : (soId % 3 == 1 ? warehouse2 : warehouse3));
            dn.setStatus(DispatchNoteStatus.DRAFT);

            List<DispatchNoteItem> dItems = new ArrayList<>();
            for (long productId = 1; productId <= 3; productId++) {
                DispatchNoteItem di = new DispatchNoteItem();
                di.setDispatchNote(dn);
                di.setProductId(productId);
                di.setDispatchedQuantity(5 + (int)(Math.random()*15));
                di.setSellingPrice(BigDecimal.valueOf(6 + Math.random()*5));
                di.setDiscount(BigDecimal.valueOf(Math.random()*1));
                di.setTaxRate(BigDecimal.valueOf(20.0));
                dItems.add(di);
            }
            dn.setItems(dItems);
            dispatchNoteRepository.save(dn);
        }

        System.out.println("Warehouse data loaded successfully with 3 warehouses, 10 products per warehouse, and realistic Receipt/Dispatch notes!");
    }
}
