package com.dimitrijevic175.sales_service;

import com.dimitrijevic175.sales_service.configuration.WarehouseServiceClient;
import com.dimitrijevic175.sales_service.domain.Customer;
import com.dimitrijevic175.sales_service.domain.SalesOrder;
import com.dimitrijevic175.sales_service.dto.*;
import com.dimitrijevic175.sales_service.repository.CustomerRepository;
import com.dimitrijevic175.sales_service.repository.SalesOrderRepository;
import com.dimitrijevic175.sales_service.service.impl.SalesOrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SalesOrderRepository salesOrderRepository;

    @Mock
    private WarehouseServiceClient warehouseClient;

    @InjectMocks
    private SalesOrderServiceImpl salesOrderService;

    private CreateSalesOrderRequestDto request;
    private Customer customer;

    @BeforeEach
    void setUp() {
        request = createSalesOrderRequest();

        customer = new Customer();
        customer.setId(1L);
        customer.setEmail("test@mail.com");
    }

    @Test
    void shouldCreateSalesOrderSuccessfully() {

        // Priprema podataka
        Long warehouseId = 10L;

        CheckAvailabilityResponseDto availability = new CheckAvailabilityResponseDto();
        availability.setWarehouseId(warehouseId);

        SalesOrder savedOrder = new SalesOrder();
        savedOrder.setId(100L);

        // kada se pozovu side effektovi, da vrati nase podatke koje smo setovali
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(warehouseClient.checkAvailability(any())).thenReturn(availability);
        when(salesOrderRepository.save(any(SalesOrder.class))).thenReturn(savedOrder);

        // Pozivam sut da testiram metodu
        SalesOrderResponseDto response = salesOrderService.createSalesOrder(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getCustomerId());
        assertEquals(warehouseId, response.getWarehouseId());
        assertEquals("CREATED", response.getStatus());
        assertEquals(1, response.getItems().size());

        // VERIFY
        verify(customerRepository).findById(1L);
        verify(warehouseClient).checkAvailability(any());
        verify(warehouseClient).createDispatchNote(any(DispatchNoteRequestDto.class));
        verify(salesOrderRepository).save(any(SalesOrder.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {

        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // pozivam sut da testiram metodu, s obzirom da baca exception hvatam ga.
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> salesOrderService.createSalesOrder(request)
        );

        // proveravam da li je exception ocekivan,
        assertEquals("Customer not found", exception.getMessage());

        // VERIFY
        verify(customerRepository).findById(anyLong());
        verifyNoInteractions(warehouseClient);
        verify(salesOrderRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNoWarehouseAvailable() {

        CheckAvailabilityResponseDto availability = new CheckAvailabilityResponseDto();
        availability.setWarehouseId(null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(warehouseClient.checkAvailability(any())).thenReturn(availability);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> salesOrderService.createSalesOrder(request)
        );

        assertEquals("No warehouse can fulfill the requested order", exception.getMessage());

        // VERIFY
        verify(warehouseClient).checkAvailability(any());
        verify(warehouseClient, never()).createDispatchNote(any());
        verify(salesOrderRepository, never()).save(any());
    }


    private CreateSalesOrderRequestDto createSalesOrderRequest() {

        CreateSalesOrderRequestDto request = new CreateSalesOrderRequestDto();

        request.setCustomerId(1L);

        CreateSalesOrderRequestDto.CreateSalesOrderItemDto item = new CreateSalesOrderRequestDto.CreateSalesOrderItemDto();

        item.setProductId(101L);
        item.setQuantity(2);
        item.setDiscount(BigDecimal.ZERO);
        item.setTaxRate(BigDecimal.valueOf(20));
        item.setSellingPrice(BigDecimal.valueOf(100));

        request.setItems(List.of(item));

        return request;
    }
}
