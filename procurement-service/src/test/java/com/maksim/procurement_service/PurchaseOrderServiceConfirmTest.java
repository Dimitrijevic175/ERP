package com.maksim.procurement_service;

import com.maksim.procurement_service.domain.PurchaseOrder;
import com.maksim.procurement_service.domain.PurchaseOrderStatus;
import com.maksim.procurement_service.repository.PurchaseOrderRepository;
import com.maksim.procurement_service.service.impl.PurchaseOrderServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.*;
import org.mockito.*;
import org.mockito.junit.jupiter.*;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PurchaseOrderServiceConfirmTest {

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
    void confirmPurchaseOrder_success() {
        po.setStatus(PurchaseOrderStatus.SUBMITTED);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));
        when(purchaseOrderRepository.save(any())).thenReturn(po);

        String result = sut.confirmPurchaseOrder(1L);

        assertEquals("Purchase Order #1 confirmed successfully.", result);
        assertEquals(PurchaseOrderStatus.CONFIRMED, po.getStatus());
        verify(purchaseOrderRepository).save(po);
    }

    @Test
    void confirmPurchaseOrder_notFound_throwsException() {
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> sut.confirmPurchaseOrder(1L));
        assertEquals("Purchase Order not found", ex.getMessage());
        verify(purchaseOrderRepository, never()).save(any());
    }

    @Test
    void confirmPurchaseOrder_wrongStatus_throwsException() {
        po.setStatus(PurchaseOrderStatus.DRAFT);
        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(po));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> sut.confirmPurchaseOrder(1L));
        assertEquals("Only submitted Purchase Orders can be confirmed", ex.getMessage());
        verify(purchaseOrderRepository, never()).save(any());
    }
}

