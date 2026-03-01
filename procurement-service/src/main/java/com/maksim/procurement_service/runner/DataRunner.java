package com.maksim.procurement_service.runner;

import com.maksim.procurement_service.domain.*;
import com.maksim.procurement_service.repository.PurchaseOrderRepository;
import com.maksim.procurement_service.repository.SupplierRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataRunner {

    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @PostConstruct
    public void initData() {

        if (supplierRepository.count() > 0) return;

        // ================= SUPPLIERS =================
        Supplier supplier1 = createSupplier(
                "ABC Logistics d.o.o.",
                "Bulevar Kralja Aleksandra 123, Beograd",
                "123456789",
                "98765432",
                "Marko Marković",
                "malidima97@gmail.com",
                "+381641234567"
        );

        Supplier supplier2 = createSupplier(
                "XYZ Trade d.o.o.",
                "Nemanjina 45, Novi Sad",
                "987654321",
                "12345678",
                "Jelena Jovanović",
                "jelena@xyz.rs",
                "+381641112233"
        );

        Supplier supplier3 = createSupplier(
                "Gradnja Promet d.o.o.",
                "Cara Dušana 77, Niš",
                "555666777",
                "22233344",
                "Nikola Petrović",
                "nikola@gradnja.rs",
                "+381642223334"
        );

        Supplier supplier4 = createSupplier(
                "Metal Invest d.o.o.",
                "Industrijska zona bb, Kragujevac",
                "444555666",
                "11122233",
                "Ivana Ilić",
                "ivana@metalinvest.rs",
                "+381645556667"
        );

        // ================= PURCHASE ORDERS =================
        createPO(1L, supplier1, PurchaseOrderStatus.RECEIVED, List.of(
                new ProductData(1L, 50, 4.50),
                new ProductData(2L, 40, 5.00)
        ));

        createPO(2L, supplier2, PurchaseOrderStatus.SUBMITTED, List.of(
                new ProductData(3L, 25, 6.00),
                new ProductData(4L, 10, 20.00)
        ));

        createPO(3L, supplier3, PurchaseOrderStatus.RECEIVED, List.of(
                new ProductData(5L, 15, 18.00),
                new ProductData(6L, 20, 25.00)
        ));

        createPO(1L, supplier4, PurchaseOrderStatus.SUBMITTED, List.of(
                new ProductData(7L, 500, 0.30)
        ));

        createPO(2L, supplier4, PurchaseOrderStatus.RECEIVED, List.of(
                new ProductData(8L, 600, 0.40)
        ));

        createPO(1L, supplier1, PurchaseOrderStatus.CLOSED, List.of(
                new ProductData(9L, 200, 1.20)
        ));

        createPO(3L, supplier2, PurchaseOrderStatus.RECEIVED, List.of(
                new ProductData(10L, 1000, 0.60)
        ));

        createPO(2L, supplier3, PurchaseOrderStatus.SUBMITTED, List.of(
                new ProductData(11L, 800, 1.10)
        ));

        createPO(1L, supplier4, PurchaseOrderStatus.RECEIVED, List.of(
                new ProductData(12L, 120, 15.00)
        ));

        System.out.println("Purchase Orders i Supplier-i uspešno ubačeni!");
    }

    // ================= HELPER METODE =================

    private Supplier createSupplier(String name, String address,
                                    String taxNumber, String regNumber,
                                    String contactName, String email, String phone) {

        Supplier supplier = new Supplier();
        supplier.setName(name);
        supplier.setAddress(address);
        supplier.setTaxNumber(taxNumber);
        supplier.setRegistrationNumber(regNumber);
        supplier.setActive(true);

        SupplierContact contact = new SupplierContact();
        contact.setFullName(contactName);
        contact.setEmail(email);
        contact.setPhone(phone);
        contact.setSupplier(supplier);

        supplier.setContacts(List.of(contact));

        return supplierRepository.save(supplier);
    }

    /**
     * Kreira Purchase Order sa više proizvoda
     */
    private void createPO(Long warehouseId,
                          Supplier supplier,
                          PurchaseOrderStatus status,
                          List<ProductData> products) {

        PurchaseOrder po = new PurchaseOrder();
        po.setWarehouseId(warehouseId);
        po.setSupplier(supplier);
        po.setStatus(status);
        po.setSubmittedAt(LocalDateTime.now().minusDays((long) (Math.random() * 30)));

        if (status == PurchaseOrderStatus.RECEIVED) {
            po.setReceivedAt(LocalDateTime.now());
        }

        List<PurchaseOrderItem> items = new ArrayList<>();
        for (ProductData p : products) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProductId(p.productId);
            item.setQuantity(p.quantity);
            item.setPurchasePrice(BigDecimal.valueOf(p.purchasePrice));
            item.setPurchaseOrder(po);
            items.add(item);
        }

        po.setItems(items);

        purchaseOrderRepository.save(po);
    }

    /**
     * Helper klasa za inicijalizaciju proizvoda u PO
     */
    private static class ProductData {
        private final Long productId;
        private final int quantity;
        private final double purchasePrice;

        public ProductData(Long productId, int quantity, double purchasePrice) {
            this.productId = productId;
            this.quantity = quantity;
            this.purchasePrice = purchasePrice;
        }
    }
}
