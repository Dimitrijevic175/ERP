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
@MockitoSettings(strictness = Strictness.LENIENT)
public class PurchaseOrderServiceTest {

    private PurchaseOrderServiceImpl sut; // servis koji testiramo

    @Mock
    private SupplierRepository supplierRepository;

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @Mock
    private WarehouseClient warehouseClient; // wrapper za webclient

    @Mock
    private ProductClient productClient; // wrapper za webclient

    @Mock
    private NotificationSender notificationSender;

    @Mock
    private PurchaseOrderMapper purchaseOrderMapper;

    private Supplier supplier;
    private LowStockItemDto lowStock;
    private ProductInfoDto productInfo;

    @BeforeEach
    void setUp() {
        // Injektuj mockove u servis ručno
        sut = spy(new PurchaseOrderServiceImpl(
                purchaseOrderRepository,
                supplierRepository,
                warehouseClient,
                productClient,
                notificationSender,
                purchaseOrderMapper
        ));
        doReturn(new byte[0])
                .when(sut)
                .generatePurchaseOrderPdfBytes(any(PurchaseOrder.class));

        supplier = new Supplier();
        supplier.setId(1L);
        supplier.setName("Test Supplier");

        lowStock = new LowStockItemDto();
        lowStock.setProductId(100L);
        lowStock.setQuantity(2);

        productInfo = new ProductInfoDto();
        productInfo.setId(100L);
        productInfo.setMaxQuantity(10);
        productInfo.setPurchasePrice(new BigDecimal("5.5"));
    }

    @Test
    void testCreateAutoPurchaseOrder_supplierNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        CreatePurchaseOrderRequestDto request = new CreatePurchaseOrderRequestDto();
        request.setSupplierId(1L);
        request.setWarehouseId(1L);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> sut.createAutoPurchaseOrder(request));

        assertEquals("Supplier not found", ex.getMessage());
        verify(supplierRepository).findById(1L);
    }

    @Test
    void testCreateAutoPurchaseOrder_withLowStock() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseClient.getLowStock(1L)).thenReturn(List.of(lowStock));
        when(productClient.getProductById(100L)).thenReturn(productInfo);

        PurchaseOrder savedPO = new PurchaseOrder();
        savedPO.setId(1L);
        savedPO.setWarehouseId(1L);
        savedPO.setSupplier(supplier);
        savedPO.setStatus(PurchaseOrderStatus.DRAFT);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setProductId(100L);
        item.setQuantity(8); // 10-2
        item.setPurchasePrice(productInfo.getPurchasePrice());
        savedPO.getItems().add(item);

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(savedPO);

        CreatePurchaseOrderRequestDto request = new CreatePurchaseOrderRequestDto();
        request.setSupplierId(1L);
        request.setWarehouseId(1L);

        PurchaseOrderResponseDto response = sut.createAutoPurchaseOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(1, response.getItems().size());
        assertEquals(8, response.getItems().get(0).getQuantity());
        assertEquals(productInfo.getPurchasePrice(), response.getItems().get(0).getPurchasePrice());

        // proveri da li je repo pozvan sa ispravnim itemom
        ArgumentCaptor<PurchaseOrder> poCaptor = ArgumentCaptor.forClass(PurchaseOrder.class);
        verify(purchaseOrderRepository).save(poCaptor.capture());

        PurchaseOrder captured = poCaptor.getValue();
        assertEquals(1, captured.getItems().size());
        assertEquals(100L, captured.getItems().get(0).getProductId());
    }

    @Test
    void testCreateAutoPurchaseOrder_noLowStock() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
        when(warehouseClient.getLowStock(1L)).thenReturn(List.of());

        PurchaseOrder savedPO = new PurchaseOrder();
        savedPO.setId(1L);
        savedPO.setWarehouseId(1L);
        savedPO.setSupplier(supplier);
        savedPO.setStatus(PurchaseOrderStatus.DRAFT);

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenReturn(savedPO);

        CreatePurchaseOrderRequestDto request = new CreatePurchaseOrderRequestDto();
        request.setSupplierId(1L);
        request.setWarehouseId(1L);

        PurchaseOrderResponseDto response = sut.createAutoPurchaseOrder(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(0, response.getItems().size());
    }

    @Test
    void submitPurchaseOrder_poNotFound_throwsException() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> sut.submitPurchaseOrder(1L));

        assertEquals("Purchase Order not found", ex.getMessage());
        verify(purchaseOrderRepository).findById(1L);
        verifyNoMoreInteractions(purchaseOrderRepository);
    }

    @Test
    void submitPurchaseOrder_notDraft_throwsException() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(PurchaseOrderStatus.SUBMITTED);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> sut.submitPurchaseOrder(1L));

        assertEquals("Purchase order already submitted or closed", ex.getMessage());
        verify(purchaseOrderRepository).findById(1L);
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void submitPurchaseOrder_addsMissingItems_andSubmits() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setWarehouseId(10L);

        // već ima jedan item
        PurchaseOrderItem existing = new PurchaseOrderItem();
        existing.setProductId(100L);
        po.getItems().add(existing);

        LowStockItemDto lowStock1 = new LowStockItemDto();
        lowStock1.setProductId(100L); // već postoji

        LowStockItemDto lowStock2 = new LowStockItemDto();
        lowStock2.setProductId(200L); // treba da se doda

        ProductInfoDto product200 = new ProductInfoDto();
        product200.setId(200L);
        product200.setMaxQuantity(50);
        product200.setPurchasePrice(new BigDecimal("12.5"));

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(warehouseClient.getLowStock(10L)).thenReturn(List.of(lowStock1, lowStock2));
        when(productClient.getProductById(200L)).thenReturn(product200);

        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = sut.submitPurchaseOrder(1L);

        assertEquals("Purchase order submitted successfully", result);
        assertEquals(PurchaseOrderStatus.SUBMITTED, po.getStatus());
        assertNotNull(po.getSubmittedAt());

        assertEquals(2, po.getItems().size());

        PurchaseOrderItem added = po.getItems().stream()
                .filter(i -> i.getProductId().equals(200L))
                .findFirst()
                .orElseThrow();

        assertEquals(50, added.getQuantity());
        assertEquals(product200.getPurchasePrice(), added.getPurchasePrice());

        verify(purchaseOrderRepository).save(po);
    }


    @Test
    void submitPurchaseOrder_noLowStock_submitsOnly() {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(1L);
        po.setStatus(PurchaseOrderStatus.DRAFT);
        po.setWarehouseId(10L);

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(warehouseClient.getLowStock(10L)).thenReturn(List.of());
        when(purchaseOrderRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        String result = sut.submitPurchaseOrder(1L);

        assertEquals("Purchase order submitted successfully", result);
        assertEquals(PurchaseOrderStatus.SUBMITTED, po.getStatus());
        assertEquals(0, po.getItems().size());
    }


}
