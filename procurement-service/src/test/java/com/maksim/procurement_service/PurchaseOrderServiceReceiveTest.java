package com.maksim.procurement_service;

import com.maksim.procurement_service.domain.PurchaseOrder;
import com.maksim.procurement_service.domain.PurchaseOrderStatus;
import com.maksim.procurement_service.repository.PurchaseOrderRepository;
import com.maksim.procurement_service.service.impl.PurchaseOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PurchaseOrderServiceReceiveTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;

    @InjectMocks
    private PurchaseOrderServiceImpl sut;

    private PurchaseOrder po;

    @BeforeEach
    void setUp() {
        po = new PurchaseOrder();
        po.setId(1L);
    }

    @Test
    void receivePurchaseOrder_success() {
        po.setStatus(PurchaseOrderStatus.CONFIRMED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(any())).thenReturn(po);

        sut.receivePurchaseOrder(1L);

        assertEquals(PurchaseOrderStatus.RECEIVED, po.getStatus());
        verify(purchaseOrderRepository).save(po);
    }

    @Test
    void receivePurchaseOrder_notFound_throwsException() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> sut.receivePurchaseOrder(1L));
        assertEquals("Purchase Order not found", ex.getMessage());
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void receivePurchaseOrder_wrongStatus_throwsException() {
        po.setStatus(PurchaseOrderStatus.DRAFT); // nije CONFIRMED
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> sut.receivePurchaseOrder(1L));
        assertEquals("Only CONFIRMED purchase orders can be marked as RECEIVED", ex.getMessage());
        verify(purchaseOrderRepository, never()).save(any());
    }
}

