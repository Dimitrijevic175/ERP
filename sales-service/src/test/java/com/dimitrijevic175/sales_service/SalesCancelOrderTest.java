package com.dimitrijevic175.sales_service;

import com.dimitrijevic175.sales_service.domain.SalesOrder;
import com.dimitrijevic175.sales_service.domain.SalesOrderStatus;
import com.dimitrijevic175.sales_service.dto.DispatchNoteDto;
import com.dimitrijevic175.sales_service.dto.DispatchNoteStatusDto;
import com.dimitrijevic175.sales_service.repository.SalesOrderRepository;
import com.dimitrijevic175.sales_service.configuration.WarehouseServiceClient;
import com.dimitrijevic175.sales_service.service.impl.SalesOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesCancelOrderTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private WarehouseServiceClient warehouseWebClient;

    @InjectMocks
    private SalesOrderServiceImpl salesOrderService;

    private SalesOrder order;

    @BeforeEach
    void setUp() {
        order = new SalesOrder();
        order.setId(1L);
        order.setStatus(SalesOrderStatus.CREATED);
    }

    @Test
    void cancelOrder_success_withDraftDispatchNote() {
        // given
        DispatchNoteDto dispatchNote = new DispatchNoteDto();
        dispatchNote.setId(10L);
        dispatchNote.setStatus(DispatchNoteStatusDto.DRAFT);

        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        when(warehouseWebClient.getDispatchNoteBySalesOrderId(1L))
                .thenReturn(dispatchNote);

        // when
        salesOrderService.cancelOrder(1L);

        // then
        assertEquals(SalesOrderStatus.CLOSED, order.getStatus());
        assertNotNull(order.getClosedAt());

        verify(warehouseWebClient)
                .rollbackDispatchNote(10L);
        verify(salesOrderRepository)
                .save(order);
    }

    @Test
    void cancelOrder_success_withoutDispatchNote() {
        // given
        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        when(warehouseWebClient.getDispatchNoteBySalesOrderId(1L))
                .thenReturn(null);

        // when
        salesOrderService.cancelOrder(1L);

        // then
        assertEquals(SalesOrderStatus.CLOSED, order.getStatus());
        assertNotNull(order.getClosedAt());

        verify(warehouseWebClient, never())
                .rollbackDispatchNote(any());
        verify(salesOrderRepository)
                .save(order);
    }

    @Test
    void cancelOrder_orderNotFound_throwsException() {
        // given
        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.empty());

        // when + then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesOrderService.cancelOrder(1L)
        );

        assertEquals("Order not found", ex.getMessage());

        verifyNoInteractions(warehouseWebClient);
        verify(salesOrderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_orderAlreadyClosed_throwsException() {
        // given
        order.setStatus(SalesOrderStatus.CLOSED);

        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        // when + then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesOrderService.cancelOrder(1L)
        );

        assertEquals("Order already canceled", ex.getMessage());

        verifyNoInteractions(warehouseWebClient);
        verify(salesOrderRepository, never()).save(any());
    }

    @Test
    void cancelOrder_dispatchNoteNotDraft_throwsException() {
        // given
        DispatchNoteDto dispatchNote = new DispatchNoteDto();
        dispatchNote.setId(10L);
        dispatchNote.setStatus(DispatchNoteStatusDto.CONFIRMED);

        when(salesOrderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        when(warehouseWebClient.getDispatchNoteBySalesOrderId(1L))
                .thenReturn(dispatchNote);

        // when + then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesOrderService.cancelOrder(1L)
        );

        assertEquals(
                "Order cannot be canceled because goods are already dispatched",
                ex.getMessage()
        );

        verify(warehouseWebClient, never())
                .rollbackDispatchNote(any());
        verify(salesOrderRepository, never())
                .save(any());
    }
}

