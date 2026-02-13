package com.maksim.procurement_service;

import com.maksim.procurement_service.configuration.ProductClient;
import com.maksim.procurement_service.configuration.WarehouseClient;
import com.maksim.procurement_service.domain.*;
import com.maksim.procurement_service.dto.*;
import com.maksim.procurement_service.listener.NotificationSender;
import com.maksim.procurement_service.mapper.PurchaseOrderMapper;
import com.maksim.procurement_service.repository.PurchaseOrderRepository;
import com.maksim.procurement_service.repository.SupplierRepository;
import com.maksim.procurement_service.service.impl.PurchaseOrderServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private WarehouseClient warehouseClient;

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private PurchaseOrderServiceImpl sut;

    private Supplier supplier;

    @BeforeEach
    void setUp() {
        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");
    }

    private CreatePurchaseOrderRequestDto createRequest(Long supplierId, Long warehouseId) {
        CreatePurchaseOrderRequestDto request = new CreatePurchaseOrderRequestDto();
        request.setSupplierId(supplierId);
        request.setWarehouseId(warehouseId);
        return request;
    }

    @Test
    void shouldThrowException_whenSupplierNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> sut.createAutoPurchaseOrder(createRequest(1L, 1L)));

        assertEquals("Supplier not found", ex.getMessage());
        verify(supplierRepository).findById(1L);
        verifyNoMoreInteractions(purchaseOrderRepository);
    }

    @Test
    void shouldCreateAutoPurchaseOrder_withLowStockItems() {
        // priprema low stock i proizvod
        LowStockItemDto lowStock = new LowStockItemDto();
        lowStock.setProductId(100L);
        lowStock.setQuantity(2);

        ProductInfoDto productInfo = new ProductInfoDto();
        productInfo.setId(100L);
        productInfo.setMaxQuantity(10);
        productInfo.setPurchasePrice(new BigDecimal("5.5"));
        productInfo.setName("Product 100");

        // save vrati isti objekat da ostanu items
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseClient.getLowStock(1L)).thenReturn(List.of(lowStock));
        when(productClient.getProductById(100L)).thenReturn(productInfo);
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> {
            PurchaseOrder po = inv.getArgument(0);
            if (po.getId() == null) {
                po.setId(1L); // simuliramo auto-generated ID iz baze
            }
            return po;
        });


        PurchaseOrderResponseDto response = sut.createAutoPurchaseOrder(createRequest(1L, 1L));

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1L, response.getId()),
                () -> assertEquals(1, response.getItems().size()),
                () -> assertEquals(8, response.getItems().get(0).getQuantity()),
                () -> assertEquals(productInfo.getPurchasePrice(), response.getItems().get(0).getPurchasePrice()),
                () -> assertEquals("Product 100", response.getItems().get(0).getProductName())
        );

        verify(purchaseOrderRepository).save(any());
    }

    @Test
    void shouldCreateAutoPurchaseOrder_withNoLowStock() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseClient.getLowStock(1L)).thenReturn(List.of());
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> {
            PurchaseOrder po = inv.getArgument(0);
            if (po.getId() == null) {
                po.setId(1L); // simuliramo auto-generated ID iz baze
            }
            return po;
        });

        PurchaseOrderResponseDto response = sut.createAutoPurchaseOrder(createRequest(1L, 1L));

        assertAll(
                () -> assertNotNull(response),
                () -> assertEquals(1L, response.getId()),
                () -> assertEquals(0, response.getItems().size())
        );
    }

    @Test
    void shouldThrowException_whenPurchaseOrderNotFound() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> sut.submitPurchaseOrder(1L));

        assertEquals("Purchase Order not found", ex.getMessage());
        verify(purchaseOrderRepository).findById(1L);
    }

    @Test
    void shouldThrowException_whenPurchaseOrderNotDraft() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(PurchaseOrderStatus.SUBMITTED);
        po.setSupplier(supplier);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> sut.submitPurchaseOrder(1L));

        assertEquals("Purchase order already submitted or closed", ex.getMessage());
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void shouldSubmitPurchaseOrder_andAddMissingItems() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setWarehouseId(10L);
        po.setSupplier(supplier);

        // već postoji jedan item
        PurchaseOrderItem existing = new PurchaseOrderItem();
        existing.setProductId(100L);
        po.getItems().add(existing);

        LowStockItemDto missing = new LowStockItemDto();
        missing.setProductId(200L);

        ProductInfoDto product200 = new ProductInfoDto();
        product200.setId(200L);
        product200.setMaxQuantity(50);
        product200.setPurchasePrice(new BigDecimal("12.5"));
        product200.setName("Product 200");

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(warehouseClient.getLowStock(10L)).thenReturn(List.of(missing));
        when(productClient.getProductById(200L)).thenReturn(product200);
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = sut.submitPurchaseOrder(1L);

        assertAll(
                () -> assertEquals("Purchase order submitted successfully", result),
                () -> assertEquals(PurchaseOrderStatus.SUBMITTED, po.getStatus()),
                () -> assertEquals(2, po.getItems().size())
        );

        verify(purchaseOrderRepository).save(po);
    }

    @Test
    void shouldSubmitPurchaseOrder_withNoMissingItems() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setWarehouseId(10L);
        po.setSupplier(supplier);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(warehouseClient.getLowStock(10L)).thenReturn(List.of());
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = sut.submitPurchaseOrder(1L);

        assertAll(
                () -> assertEquals("Purchase order submitted successfully", result),
                () -> assertEquals(PurchaseOrderStatus.SUBMITTED, po.getStatus()),
                () -> assertEquals(0, po.getItems().size())
        );

        verify(purchaseOrderRepository).save(po);
    }
}

