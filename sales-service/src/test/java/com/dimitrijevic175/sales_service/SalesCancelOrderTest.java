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
    private WarehouseServiceClient warehouseClient;

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
    void shouldCancelOrder_whenDispatchNoteIsDraft() {
        // priprema podataka
        DispatchNoteDto dispatchNote = new DispatchNoteDto();
        dispatchNote.setId(10L);
        dispatchNote.setStatus(DispatchNoteStatusDto.DRAFT);

        // vrati kada
        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(warehouseClient.getDispatchNoteBySalesOrderId(1L)).thenReturn(dispatchNote);
        when(salesOrderRepository.save(order)).thenReturn(order);

        // testiramo sut
        salesOrderService.cancelOrder(1L);

        // proveri rezultat
         assertEquals(SalesOrderStatus.CLOSED, order.getStatus());
         assertNotNull(order.getClosedAt());

        // proveri da li su se pozivi desili
        verify(warehouseClient).rollbackDispatchNote(10L);
        verify(salesOrderRepository).save(order);
    }

    @Test
    void shouldCancelOrder_whenNoDispatchNoteExists() {

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(warehouseClient.getDispatchNoteBySalesOrderId(1L)).thenReturn(null);
        when(salesOrderRepository.save(order)).thenReturn(order);

        salesOrderService.cancelOrder(1L);


       assertEquals(SalesOrderStatus.CLOSED, order.getStatus());
       assertNotNull(order.getClosedAt());

        verify(warehouseClient, never()).rollbackDispatchNote(any());
        verify(salesOrderRepository).save(order);
    }

    @Test
    void shouldThrowException_whenOrderNotFound() {

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.empty());


        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesOrderService.cancelOrder(1L)
        );

        assertEquals("Order not found", ex.getMessage());

        verifyNoInteractions(warehouseClient);
        verify(salesOrderRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenOrderAlreadyClosed() {

        order.setStatus(SalesOrderStatus.CLOSED);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesOrderService.cancelOrder(1L)
        );

        assertEquals("Order already canceled", ex.getMessage());

        verifyNoInteractions(warehouseClient);
        verify(salesOrderRepository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenDispatchNoteIsNotDraft() {

        DispatchNoteDto dispatchNote = new DispatchNoteDto();
        dispatchNote.setId(10L);
        dispatchNote.setStatus(DispatchNoteStatusDto.CONFIRMED);

        when(salesOrderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(warehouseClient.getDispatchNoteBySalesOrderId(1L)).thenReturn(dispatchNote);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesOrderService.cancelOrder(1L)
        );

        assertEquals(
                "Order cannot be canceled because goods are already dispatched",
                ex.getMessage()
        );

        verify(warehouseClient, never()).rollbackDispatchNote(any());
        verify(salesOrderRepository, never()).save(any());
    }
}


