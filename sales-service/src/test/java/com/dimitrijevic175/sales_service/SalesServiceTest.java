package com.dimitrijevic175.sales_service;

import com.dimitrijevic175.sales_service.configuration.WarehouseServiceClient;
import com.dimitrijevic175.sales_service.domain.Customer;
import com.dimitrijevic175.sales_service.domain.SalesOrder;
import com.dimitrijevic175.sales_service.dto.*;
import com.dimitrijevic175.sales_service.repository.CustomerRepository;
import com.dimitrijevic175.sales_service.repository.SalesOrderRepository;
import com.dimitrijevic175.sales_service.service.impl.SalesOrderServiceImpl;
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
    private WarehouseServiceClient warehouseWebClient;

    @InjectMocks
    private SalesOrderServiceImpl salesService;

    @Test
    void shouldCreateSalesOrderSuccessfully() {
        // given
        Long customerId = 1L;
        Long warehouseId = 10L;

        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setEmail("test@mail.com");

        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        CheckAvailabilityResponseDto availabilityResponse =
                new CheckAvailabilityResponseDto();
        availabilityResponse.setWarehouseId(warehouseId);

        when(warehouseWebClient.checkAvailability(any()))
                .thenReturn(availabilityResponse);

        when(salesOrderRepository.save(any(SalesOrder.class)))
                .thenAnswer(invocation -> {
                    SalesOrder so = invocation.getArgument(0);
                    so.setId(100L);
                    return so;
                });

        CreateSalesOrderRequestDto request =
                createSalesOrderRequest();

        // when
        SalesOrderResponseDto response =
                salesService.createSalesOrder(request);

        // then
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(customerId, response.getCustomerId());
        assertEquals(warehouseId, response.getWarehouseId());
        assertEquals("CREATED", response.getStatus());
        assertEquals(1, response.getItems().size());

        verify(warehouseWebClient)
                .createDispatchNote(any(DispatchNoteRequestDto.class));
        verify(salesOrderRepository)
                .save(any(SalesOrder.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        // given
        when(customerRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        CreateSalesOrderRequestDto request =
                createSalesOrderRequest();

        // when / then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesService.createSalesOrder(request)
        );

        assertEquals("Customer not found", ex.getMessage());

        verifyNoInteractions(warehouseWebClient);
        verify(salesOrderRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenNoWarehouseAvailable() {
        // given
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(anyLong()))
                .thenReturn(Optional.of(customer));

        CheckAvailabilityResponseDto response =
                new CheckAvailabilityResponseDto();
        response.setWarehouseId(null);

        when(warehouseWebClient.checkAvailability(any()))
                .thenReturn(response);

        CreateSalesOrderRequestDto request =
                createSalesOrderRequest();

        // when / then
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> salesService.createSalesOrder(request)
        );

        assertEquals(
                "No warehouse can fulfill the requested order",
                ex.getMessage()
        );

        verify(salesOrderRepository, never()).save(any());
        verify(warehouseWebClient, never())
                .createDispatchNote(any());
    }


    private CreateSalesOrderRequestDto createSalesOrderRequest() {
        CreateSalesOrderRequestDto request = new CreateSalesOrderRequestDto();
        request.setCustomerId(1L);

        CreateSalesOrderRequestDto.CreateSalesOrderItemDto item =
                new CreateSalesOrderRequestDto.CreateSalesOrderItemDto();

        item.setProductId(101L);
        item.setQuantity(2);
        item.setDiscount(BigDecimal.ZERO);
        item.setTaxRate(BigDecimal.valueOf(20));
        item.setSellingPrice(BigDecimal.valueOf(100));

        request.setItems(List.of(item));
        return request;
    }

}
