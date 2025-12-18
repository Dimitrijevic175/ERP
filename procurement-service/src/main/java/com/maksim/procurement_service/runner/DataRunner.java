package com.maksim.procurement_service.runner;

import com.maksim.procurement_service.domain.*;
import com.maksim.procurement_service.repository.PurchaseOrderRepository;
import com.maksim.procurement_service.repository.SupplierRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataRunner {

    private final SupplierRepository supplierRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @PostConstruct
    public void initData() {

        // ====== Supplier 1 ======
        Supplier supplier1 = new Supplier();
        supplier1.setName("ABC Logistics d.o.o.");
        supplier1.setAddress("Bulevar Kralja Aleksandra 123, Beograd");
        supplier1.setTaxNumber("123456789");
        supplier1.setRegistrationNumber("98765432");
        supplier1.setActive(true);

        SupplierContact contact1 = new SupplierContact();
        contact1.setFullName("Marko Marković");
        contact1.setEmail("marko@abc.rs");
        contact1.setPhone("+381641234567");
        contact1.setSupplier(supplier1);

        supplier1.setContacts(java.util.List.of(contact1));

        supplierRepository.save(supplier1);

        // ====== Supplier 2 ======
        Supplier supplier2 = new Supplier();
        supplier2.setName("XYZ Trade doo");
        supplier2.setAddress("Nemanjina 45, Novi Sad");
        supplier2.setTaxNumber("987654321");
        supplier2.setRegistrationNumber("12345678");
        supplier2.setActive(true);

        SupplierContact contact2 = new SupplierContact();
        contact2.setFullName("Jelena Jovanović");
        contact2.setEmail("jelena@xyz.rs");
        contact2.setPhone("+381641112233");
        contact2.setSupplier(supplier2);

        supplier2.setContacts(java.util.List.of(contact2));

        supplierRepository.save(supplier2);

        // ====== PurchaseOrder 1 ======
        PurchaseOrder po1 = new PurchaseOrder();
        po1.setWarehouseId(1L); // primer warehouse ID
        po1.setSupplier(supplier1);
        po1.setStatus(PurchaseOrderStatus.RECEIVED);
        po1.setReceivedAt(LocalDateTime.now());
        po1.setSubmittedAt(LocalDateTime.of(2025,12,14,12,0,0));

        PurchaseOrderItem item1 = new PurchaseOrderItem();
        item1.setProductId(1L);
        item1.setQuantity(10);
        item1.setPurchasePrice(new BigDecimal("125.50"));
        item1.setPurchaseOrder(po1);

        po1.setItems(java.util.List.of(item1));

        purchaseOrderRepository.save(po1);

        // ====== PurchaseOrder 2 ======
        PurchaseOrder po2 = new PurchaseOrder();
        po2.setWarehouseId(2L);
        po2.setSupplier(supplier2);
        po2.setStatus(PurchaseOrderStatus.SUBMITTED);
        po2.setSubmittedAt(LocalDateTime.of(2025,4,3,9,30,0));

        PurchaseOrderItem item2 = new PurchaseOrderItem();
        item2.setProductId(2L);
        item2.setQuantity(5);
        item2.setPurchasePrice(new BigDecimal("250.00"));
        item2.setPurchaseOrder(po2);

        po2.setItems(java.util.List.of(item2));

        purchaseOrderRepository.save(po2);

        System.out.println("Mokovani podaci za Supplier i PurchaseOrder ubaceni u bazu!");
    }
}
